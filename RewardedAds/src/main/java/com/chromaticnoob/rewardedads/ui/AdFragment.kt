package com.chromaticnoob.rewardedads.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chromaticnoob.rewardedads.AdConstants.BITRATE_CHOOSING_MODE_HIGHEST_RES
import com.chromaticnoob.rewardedads.AdConstants.BITRATE_CHOOSING_MODE_LOWEST_RES
import com.chromaticnoob.rewardedads.AdConstants.BITRATE_CHOOSING_MODE_SMART
import com.chromaticnoob.rewardedads.AdConstants.IMPRESSION_MODE_THREE_SECONDS
import com.chromaticnoob.rewardedads.AdConstants.IMPRESSION_MODE_UPON_FULL_WATCH
import com.chromaticnoob.rewardedads.AdConstants.ORIENTATION_AD_DEPENDING
import com.chromaticnoob.rewardedads.AdViewModel
import com.chromaticnoob.rewardedads.R
import com.chromaticnoob.rewardedads.RewardedAdsCallback
import com.chromaticnoob.rewardedads.databinding.AdFragmentBinding
import com.chromaticnoob.rewardedads.networkutils.TrafficUtils
import com.chromaticnoob.rewardedads.vastutils.Creative
import com.chromaticnoob.rewardedads.vastutils.MediaFile
import com.chromaticnoob.rewardedads.vastutils.VastObject
import com.chromaticnoob.rewardedads.vastutils.VastParser
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong

class AdFragment : Fragment() {
    /********************************
     * Storing UI-related variables *
     ********************************/
    private var _binding: AdFragmentBinding? = null
    private val binding get() = _binding!!
    var callbackBroadcasters: MutableList<RewardedAdsCallback> = mutableListOf()
    private val gson: Gson = GsonBuilder().create()
    private var adPlayer: ExoPlayer? = null
    private var adVast: String = ""
    private lateinit var adViewModel: AdViewModel
    private var muted: Boolean = false
    private var currentMediaSequence = 1
    private var adDuration: Long = 0
    private var remainingDuration: Long = 0
    private var currentCreative: Creative? = null
    private var currentTrackingEvents: MutableMap<String, String>? = null
    private var currentMediaFile: MediaFile? = null
    private var registeredTrackingEvents: MutableList<String> = mutableListOf()
    private var eventsToRegisterOnce: List<String> = listOf("start", "creativeView", "complete", "firstQuartile", "midpoint", "thirdQuartile")
    private var maintainAspectRatio: Boolean = false


    companion object {
        fun newInstance(adJson: String) = AdFragment()
            .apply { arguments = Bundle().apply { putString("adJson", adJson) } }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AdFragmentBinding.inflate(inflater, container, false)
        adViewModel = ViewModelProvider(this)[AdViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**************************************************************************
         * Extracting the adJson information in order to build our ad bit by bit  *
         **************************************************************************/
        val json = gson.fromJson(arguments?.getString("adJson"), JsonElement::class.java) ?: JsonNull.INSTANCE
        val adjson = if (json.isJsonNull) return else json.asJsonObject
        if (adjson.has("vast")) {
            adVast = adjson.getAsJsonPrimitive("vast").asString
        }
        adViewModel.adConfig.apiEndpoint = adjson.getAsJsonPrimitive("apiendpoint").asString
        adViewModel.adConfig.httpTimeout = adjson.getAsJsonPrimitive("httptimeout").asLong
        adViewModel.adConfig.mutebutton = adjson.getAsJsonPrimitive("mutebutton").asBoolean
        adViewModel.adConfig.impressionMode = adjson.getAsJsonPrimitive("impressionmode").asInt
        adViewModel.adConfig.bitrateChoosingMode = adjson.getAsJsonPrimitive("bitratemode").asInt
        adViewModel.adConfig.orientationMode = adjson.getAsJsonPrimitive("orientationmode").asInt
        adViewModel.adConfig.debugging = adjson.getAsJsonPrimitive("debugging").asBoolean
        adViewModel.adConfig.scalingMode = adjson.getAsJsonPrimitive("scalingmode").asInt
        adViewModel.adConfig.showCountdown = adjson.getAsJsonPrimitive("showcountdown").asBoolean
        adViewModel.adConfig.countDownTextCaption = adjson.getAsJsonPrimitive("countdowntextcaption").asString
        adViewModel.adConfig.countDownTextSize = adjson.getAsJsonPrimitive("countdowntextsize").asFloat
        adViewModel.adConfig.countDownColor = adjson.getAsJsonPrimitive("countdowntextcolor").asInt
        adViewModel.adConfig.showProgressCircle = adjson.getAsJsonPrimitive("showprogresscircle").asBoolean
        adViewModel.adConfig.progresscircleColor = adjson.getAsJsonPrimitive("progresscirclecolor").asInt
        adViewModel.adConfig.exitButton = adjson.getAsJsonPrimitive("exitbutton").asBoolean
        adViewModel.adConfig.clicking = adjson.getAsJsonPrimitive("clicking").asBoolean
        adViewModel.adConfig.repeatmode = adjson.getAsJsonPrimitive("repeatmode").asBoolean
        adViewModel.adConfig.minimumbuffering = adjson.getAsJsonPrimitive("minimumbuffering").asInt

        /**************************************************************************************
         *  Fetching ad from network, this is a synchronous call, therefore it will lock a certain thread
         *  If there is already a custom vast passed by the user (for test purposes for example), then
         *  the network call will be skipped
         */

        var answerVast = ""
        if (adVast == "") {
            Thread {
                val client = OkHttpClient().newBuilder()
                    .connectTimeout(adViewModel.adConfig.httpTimeout, TimeUnit.SECONDS)
                    .readTimeout(adViewModel.adConfig.httpTimeout, TimeUnit.SECONDS)
                    .writeTimeout(adViewModel.adConfig.httpTimeout, TimeUnit.SECONDS)
                    .build()
                val request: Request = Request.Builder()
                    .url(adViewModel.adConfig.apiEndpoint)
                    .method("GET", null)
                    .build()
                val response: Response = client.newCall(request).execute()
                answerVast = response.body?.string() ?: ""

                if (answerVast == "") {
                    Log.e(
                        "RewardedAdsERROR",
                        "The API endpoint url returned no valid VAST response."
                    )
                    for (listener in callbackBroadcasters) {
                        listener.onAdFetchFailed("The API endpoint url returned no valid VAST response.")
                        closeAd()
                    }
                }
            }.start()
        }
        while (answerVast=="") {
            0
        }
        adVast = answerVast
        /** Parsing XML and passing its various info to the viewmodel **/
        val tempad = VastParser.parseXML(adVast)
        if (tempad == VastObject()) {
            adViewModel.logThis("INVALIDVastException - There was an issue parsing the VAST response.")
            for (listener in callbackBroadcasters) {
                listener.onAdFetchFailed("INVALIDVastException - There was an issue parsing the VAST response.")
            }
            closeAd()
            return
        } else {
            adViewModel.adVastObject = tempad
        }
        val creatives = adViewModel.adVastObject.creativeList
        currentCreative = creatives[0]
        currentTrackingEvents = currentCreative?.trackingEvents
        val mediaFiles = currentCreative?.mediaList

        /* Deciding which bitrate to choose from the mediafile list depending on the setting mode */
        val bitrateList: MutableList<Int> = mutableListOf()
        var chosenBitrate: Int? = 0
        if (mediaFiles != null) {
            for (media in mediaFiles) {
                media.bitrate?.let { bitrateList.add(it) }
            }
        }
        when (adViewModel.adConfig.bitrateChoosingMode) {
            BITRATE_CHOOSING_MODE_LOWEST_RES -> {
                chosenBitrate = bitrateList.toIntArray().minOrNull()
            }
            BITRATE_CHOOSING_MODE_HIGHEST_RES -> {
                chosenBitrate = bitrateList.toIntArray().maxOrNull()
            }
            BITRATE_CHOOSING_MODE_SMART -> {
                /* How it works: it starts loading the lowest quality, then increases it
                 * when detecting a network that can do better. */
                chosenBitrate = bitrateList.toIntArray().minOrNull()
            }
        }
        var chosenMediaUrl = ""
        if (mediaFiles != null) {
            for (media in mediaFiles) {
                if (media.bitrate == chosenBitrate) {
                    chosenMediaUrl = media.url.toString()
                    currentMediaFile = media
                }
            }
        }

        /********************************
         * Building our player instance *
         ********************************/
        val loadControl =
            DefaultLoadControl.Builder()
                .setBufferDurationsMs(adViewModel.adConfig.minimumbuffering,
                    adViewModel.adConfig.minimumbuffering*15,
                    adViewModel.adConfig.minimumbuffering,
                    adViewModel.adConfig.minimumbuffering)
                .build()

        adPlayer = ExoPlayer.Builder(requireContext())
            .setLoadControl(loadControl)
            .build() //Building the player.
        binding.cnAdplayer.player = adPlayer //Binding our PlayerView to our player instance.
        adPlayer?.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
        //Play as soon as media is buffered and ready
        adPlayer?.playWhenReady = true
        adViewModel.working = true
        /********************************
         * Attaching VAST Media to Player *
         ********************************/
        val vid = MediaItem.Builder().setUri(chosenMediaUrl).build() //Building MediaItem
        adPlayer?.setMediaItem(vid) //Assigning the MediaItem to the player.
        adPlayer?.prepare() //Preparing our player to avoid playback problems upon buffering.
        /** These methods are used to smartly choose the bitrate if it's the selected mode **/
        val currentProg = adPlayer?.currentPosition
        if (adViewModel.adConfig.bitrateChoosingMode == BITRATE_CHOOSING_MODE_SMART) {
            val networkSpeed = TrafficUtils.getNetworkSpeed(1000)
            var suitableBitrate = chosenBitrate
            for (bitrate in bitrateList.toIntArray().sortedDescending()) {
                if ((networkSpeed - bitrate) > 0) {
                    suitableBitrate = bitrate
                    if (mediaFiles != null) {
                        for (media in mediaFiles) {
                            if (media.bitrate == suitableBitrate) {
                                currentMediaFile = media
                                adPlayer?.setMediaItem(MediaItem.Builder().setUri(media.url.toString()).build())
                                adPlayer?.prepare()
                                if (currentProg != null) {
                                    adPlayer?.seekTo(currentProg)
                                }
                            }
                        }
                    }
                    break
                }
            }
        }

        /** Letting our listeners know the ad has been loaded into the player and is now buffering **/
        for (broadcaster in callbackBroadcasters) {
            broadcaster.adLoaded()
        }

        /* Setting maintainAspectRatio on if exists */
        maintainAspectRatio = currentMediaFile?.maintianAspectRatio == true

        val adDurationText = currentCreative?.duration ?: "0"
        val adSeconds = adDurationText.takeLast(2)
        val adMinutes = adDurationText.dropLast(3).takeLast(2)
        val adHours = adDurationText.take(2)
        adDuration = (adSeconds.toLong() + (adMinutes.toLong() * 60) + (adHours.toLong() * 3600)) * 1000
        /************************
         * Adjusting Scale Mode *
         ************************/
        if (adViewModel.adConfig.scalingMode == 0) {
            binding.cnAdplayer.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        } else {
            if (!maintainAspectRatio) {
                binding.cnAdplayer.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }
        }

        /** Now registering the fullscreen event since rewarded ads are always fullscreen **/
        registerTrackingEvent("fullscreen")

        /** Displaying/Hiding the buttons depending on the settings before the ad was loaded **/
        if (!adViewModel.adConfig.mutebutton) {
            binding.cnMuteead.visibility = View.GONE
        }
        if (!adViewModel.adConfig.exitButton) {
            binding.cnClosead.visibility = View.GONE
        }

        /** Responding to mute button events **/
        binding.cnMuteead.setOnClickListener {
            if (muted) {
                binding.cnMuteead.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_unmuted))
                binding.cnAdplayer.player?.volume = 1f

                /* Registering the mute TrackingEvent if it exists */
                registerTrackingEvent("mute")
            } else {
                binding.cnMuteead.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_muted))
                binding.cnAdplayer.player?.volume = 0f

                /* Registering the unmute TrackingEvent if it exists */
                registerTrackingEvent("unmute")

            }
            muted = !muted
        }


        /** Responding to close button click event **/
        binding.cnClosead.setOnClickListener {
            for (broadcaster in callbackBroadcasters) {
                broadcaster.adClosed(
                    remainingDuration,
                    false
                )
            }

            /* Registering 'close' TrackingEvent */
            registerTrackingEvent("close")

            /* Removing the fragment entirely */
            activity?.supportFragmentManager?.beginTransaction()
                ?.remove(this)?.commit()
        }


        /** Now setting a very important player listener in order to keep track of our ad **/
        adPlayer?.addListener(object : Player.Listener {
            override fun onRenderedFirstFrame() {
                super.onRenderedFirstFrame()
                for (broadcaster in callbackBroadcasters) {
                    broadcaster.adStarted()
                }
                if (adViewModel.adConfig.impressionMode == 0) {
                    adViewModel.sendImpression()
                    adViewModel.logThis("Registering Impression. IMPRESSION_MODE_AFTER_FIRST_FRAME is selected.")
                }

                /* Registering creativeView event */
                registerTrackingEvent("creativeView")

                /* Setting a click listener since the ad is ready */
                if (adViewModel.adConfig.clicking) {
                    binding.cnClickdelegate.setOnClickListener {
                        for (broadcaster in callbackBroadcasters) {
                            broadcaster.adClicked()
                        }
                        val clicktrack = adViewModel.adVastObject.creativeList.first().videoClickTracking.toString()
                        val clickthrough = adViewModel.adVastObject.creativeList.first().videoClickThrough
                        adViewModel.sendTrack(clicktrack)
                        if (clickthrough != null) {
                            val clickIntent = Intent(Intent.ACTION_VIEW, Uri.parse(clickthrough))
                            startActivity(clickIntent)
                        }
                    }
                }
            }
        })

        /** Now managing our ad rotation. **/
        /* First, we get our ad's aspect ratio mode, whether it fits for landscape or portrait */
        val adRatio = if (currentMediaFile != null) (currentMediaFile?.height!! < currentMediaFile?.width!!) else null
        val screenRotation = activity?.windowManager?.defaultDisplay?.rotation
        var angleToRotateAd = 0

        if (adViewModel.adConfig.orientationMode == ORIENTATION_AD_DEPENDING) {
            if (adRatio == false) {
                /* Rotating the ad since it has portrait dimensions.*/
                when (screenRotation) {
                    Surface.ROTATION_0 -> {
                        angleToRotateAd = 0
                    }
                    Surface.ROTATION_90 -> {
                        angleToRotateAd = 90
                    }
                    Surface.ROTATION_180 -> {
                        angleToRotateAd = 180
                    }
                    Surface.ROTATION_270 -> {
                        angleToRotateAd = 270
                    }
                }
            } else {
                when (screenRotation) {
                    Surface.ROTATION_0 -> {
                        angleToRotateAd = -90
                    }
                    Surface.ROTATION_90 -> {
                        angleToRotateAd = 0
                    }
                    Surface.ROTATION_180 -> {
                        angleToRotateAd = -90
                    }
                    Surface.ROTATION_270 -> {
                        angleToRotateAd = 0
                    }
                }
            }
        }
        binding.cnAdplayerRotator.angle = angleToRotateAd


        /** Now customizing the countdown caption **/
        binding.cnAdcaption.setTextSize(COMPLEX_UNIT_SP, adViewModel.adConfig.countDownTextSize)
        binding.cnAdcaption.setTextColor(adViewModel.adConfig.countDownColor)

        /** Customizing the progress circle **/
        if (!adViewModel.adConfig.showProgressCircle) {
            binding.cnAdprogress.visibility = View.GONE
        } else {
            binding.cnAdprogress.indicatorColor
        }
        /** Now settings a periodic checker to update a few things such as the progress circle **/
        if (adViewModel.adVastObject.creativeList.isEmpty()) {
            for (listener in callbackBroadcasters) {
                listener.noAdAvailable()
            }
            closeAd()
        }
        periodicChecker(binding.cnAdprogress, binding.cnAdcaption)
    }


    /***************************************************************************************
     * A periodic checker, used for tracking progress, which is very important in VAST ads *
     ***************************************************************************************/
    @SuppressLint("SetTextI18n")
    fun periodicChecker(
        progresscircle: CircularProgressIndicator,
        caption: TextView) {

        GlobalScope.launch(Dispatchers.Main) {
            while (adViewModel.working) {
                val adprogress = adPlayer?.currentPosition
                if (adprogress != null) {
                    /* Updating the UI progress circle */
                    if (adViewModel.adConfig.showProgressCircle) {
                        progresscircle.progress = (adprogress * 100 / adDuration).toInt()
                    }

                    /* Updating the UI countdown caption */
                    remainingDuration = ((adDuration - adprogress).toDouble() / 1000.0).roundToLong()
                    if (adViewModel.adConfig.showCountdown) {
                        caption.text = "${adViewModel.adConfig.countDownTextCaption} ${remainingDuration} seconds."
                    }

                    /* Recording THREE_SECONDS mode impression if the user sets it that way */
                    if (adprogress >= 3000 && adViewModel.adConfig.impressionMode == IMPRESSION_MODE_THREE_SECONDS) {
                        adViewModel.sendImpression()
                    }

                    /* Pursuing our TrackingEvent should they exist */
                    val progress = (adprogress.toDouble() * 100.0 / adPlayer!!.duration.toDouble())
                    if (progress >= 25.0) {
                        registerTrackingEvent("firstQuartile")
                    }
                    if (progress <= 1.50) {
                        registerTrackingEvent("start")
                    }
                    if (progress>=50.0) {
                        registerTrackingEvent("midpoint")
                    }
                    if (progress >= 75.0) {
                        registerTrackingEvent("thirdQuartile")
                    }
                    if (progress >= 98.0) {
                        registerTrackingEvent("complete")

                        /* Recording full-video mode impression if the user sets it that way */
                        if (adViewModel.adConfig.impressionMode == IMPRESSION_MODE_UPON_FULL_WATCH) {
                            adViewModel.sendImpression()
                        }
                    }
                }
                delay(100)
            }
        }
    }

    /***************************************************************************
     * The following section of the fragment class is for responding to events *
     **************************************************************************/

    /** Registers any tracking event that exists in the corresponding creative in the VAST. **/
    fun registerTrackingEvent(event: String) {
        val trackingEvent = currentTrackingEvents?.get(event)
        if (trackingEvent != null) {
            /** Making sure we don't fire twice an event that is supposed to be fired once **/
            if (!eventsToRegisterOnce.contains(event)
                || (eventsToRegisterOnce.contains(event) && !registeredTrackingEvents.contains(event))) {
                if (event == "complete") {
                    for (broadcaster in callbackBroadcasters) {
                        broadcaster.adWatched()
                    }
                }
                registeredTrackingEvents.add(event)
                adViewModel.sendTrack(trackingEvent)
                adViewModel.logThis("Requesting the registration of $event trackingEvent. URL: $trackingEvent")
            }
        }
    }


    /** Releasing the player upon destroying the ad to avoid exceptions **/
    override fun onDestroy() {
        adViewModel.working = false
        callbackBroadcasters.clear()
        adPlayer?.release()
        _binding = null
        super.onDestroy()
    }

    fun setCallbackListener(callbackListener: RewardedAdsCallback) {
        callbackBroadcasters.add(callbackListener)
    }


    fun closeAd() {
        for (broadcaster in callbackBroadcasters) {
            broadcaster.adClosed(
                remainingDuration,
                false
            )
        }

        activity?.supportFragmentManager?.beginTransaction()
            ?.remove(this)
            ?.commitNow()
    }


}