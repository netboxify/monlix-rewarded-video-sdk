package com.chromaticnoob.noisyfeeddemo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chromaticnoob.noisyfeeddemo.databinding.ActivityMainBinding
import com.chromaticnoob.rewardedads.AdConstants
import com.chromaticnoob.rewardedads.RewardedAdsBuilder
import com.chromaticnoob.rewardedads.RewardedAdsCallback
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** TODO Step 1: First of all, you instantiate the builder class. This SDK uses the builder
             pattern. We can see such pattern being used in so many famous tools such as ExoPlayer and Gson,
             Therefore, the first basic thing you need to do is create an instance of the builder class.
             From there, we can configure our ad, obtain an ad fragment, and the build our ad. **/
        val adBuilder = RewardedAdsBuilder()

        /** TODO Step 2: Now you can make the choice of whether to use a custom configuration for the ad or not.
             Since this is a demo for everything, I am gonna show you how to configure the ad :
             First, you call 'configure()' on the adBuilder variable we just created.
             This is not the necessary way to do it, you can just create a RewardedAdsConfiguration instance
             by calling 'RewardedAdsConfiguration()', it's literally the same thing.**/
        val adConfiguration = adBuilder.configure() //same as RewardedAdsConfiguration()

        /** TODO Step 3: Now you can configure the ad as you like, using the instance we just created<
         *   The RewardedAdsConfiguration class contains extended methods to help you put the library
         *   to optimal usage. All ad-relevant settings are contained within it. **/

        /* Using a custom API Endpoint to retrieve a VAST 2.0 Ad-tag. If you don't specify a custom API Endpoint
           then, the default one (integrated and made for this library). If you use the default API endpoint,
           you should skip changing this, but you'll have to define 'userId', 'zoneId' and 'publisherId'
           for our network to retrieve ads specific for your app. */
        adConfiguration.apiEndpoint = "https://ytvastqa.appspot.com/static/sampletag/vast.xml"

        /* First, as a developer you need to debug the library in order to know
         * what's happening internally, however, avoid leaving the debugging option on when you
         * release your app for public use as that is not good for performance nor best practices.*/
        adConfiguration.debugging = true // Enabling debugging.

        /* mutebutton is used to disable mute button. I don't wanna do that in the demo
         * project so I am just leaving this here for you to know there is this method. */
        adConfiguration.mutebutton = true

        /* impressionMode is used to tell the library when to register an impression.
         * By default, the library will fire an impression tracking once the ad is shown.
         * But in this example, I am using a different constant. Refer to AdConstants for more.*/
        adConfiguration.impressionMode = AdConstants.IMPRESSION_MODE_THREE_SECONDS

        /* bitrateChoosingMode is used to let the library decide which mode to follow when
         * choosing mediafiles from a VAST's creative when there are multiple ones.*/
        adConfiguration.bitrateChoosingMode = AdConstants.BITRATE_CHOOSING_MODE_SMART //EXPERIMENTAL!!

        /* orientationMode will determine the mode as per which the SDK library rotates the ad.
         * There are two modes:
         * ORIENTATION_MODE__HOST_ACTIVITY_DEPENDING: means that the player will just
         * do nothing to the ad fragment. It will not rotate the fragment nor calculate the dimensions
         * of the video. This mode is useful for games and always-landscape apps. Because the majority
         * of ads are in landscape mode (Videos' height is small in comparison with their width).
         *
         * ORIENTATION_AD_DEPENDING: indicates that the player will rotate the ad fragment depending
         * on the dimensions (resolution) of the ad media.
         */
        adConfiguration.orientationMode = AdConstants.ORIENTATION_AD_DEPENDING


        /* You can even customize the countdown caption text and whether to show it or not */
        adConfiguration.showCountdown = true
        adConfiguration.countDownColor = Color.WHITE
        adConfiguration.countDownTextSize = 15f
        adConfiguration.countDownTextCaption = "The Ad will close in"

        /* These two options are related to the progress circle, usually you'd want this to be visible */
        adConfiguration.showProgressCircle = true
        adConfiguration.progresscircleColor = Color.WHITE

        /* If you want to make the close button invisible, set this to false */
        adConfiguration.exitButton = true

        /* If you want to disable clicking events, set this to false */
        adConfiguration.clicking = true

        /* If you want to put the ad on loop after it's complete, set this to true (it's true by default). */
        adConfiguration.repeatmode = true

        /* If you want to decrease the buffer length required to start playback, reduce this **/
        adConfiguration.minimumbuffering = 500 //Not recommended to set it to a value other than 1500ms.

        /** TODO Step 4: We need some condition or event that launches the ad, for example, clicking a button
             In this demo project, we have a floating button, clicking it will launch the ad. **/

        binding.fab.setOnClickListener { view ->
            /* We show a little snackbar to indicate that we're loading the ad */
            Snackbar.make(view, "Loading ad...", Snackbar.LENGTH_LONG).setAction("Action", null).show()


            /* TODO Step 5: Now use build() method on the builder instance to load the ad, as easy as that.
                You can inject the adConfiguration we have into its corresponding parameter in this build() method.
                 Using userId, zoneId, and publisherId will result in loading the ad from our database.
                  Otherwise, if you wanna use a custom API end point, avoid passing any of those 3 parameters. */
            adBuilder.build(
                hostActivity = this,
                fragmentContainer = R.id.container,
                customAdConfiguration = adConfiguration
                /* "userid", "zoneid", "37337"*/)

            /* TODO Step 6: After calling 'build()', a fragment is inflated and shown (it will be invisible
                 until there is a valid ad). Use this method in order to wait for a valid fragment to be retrieved.
                  Usually you don't need this because a fragment is instantly ready after calling 'build()',
                  but to avoid getting a null fragment instance, you can use this convenience method. */
            val adfragment = adBuilder.waitForFragment(10000) //Wait for a fragment in 10 secs, or else return null

            /* TODO Step 7: You should do anything you want with the fragment but it's not recommended to access it
                directly. In most cases, you need the ad fragment instance only to get callbacks from the ad, otherwise,
                it's really not recommended to mess with it or destroy it manually. */
            /* TODO Step 8: If you need the callbacks from the ad (Although the library will handle the internal
                events on its own) for whatever reason. You should set your listener like this : */
            adfragment?.setCallbackListener(object: RewardedAdsCallback {
                override fun adLoaded() {
                    Log.e("MyAd", "Loaded")
                }
                override fun adStarted() {
                    Log.e("MyAd", "Started")
                }
                override fun adWatched() {
                    Log.e("MyAd", "Watched")
                }
                override fun adClicked() {
                    Log.e("MyAd", "Clicked")
                }
                override fun adClosed(remainingSec: Long, manually: Boolean) {
                    Log.e("MyAd", "Closed while $remainingSec seconds remaining. Closed by user: $manually")
                }
                override fun noAdAvailable() {
                    Log.e("MyAd", "No add available.")
                }
                override fun onAdFetchFailed(error: String) {
                    Log.e("MyAd", "Error fetching Ad: $error")
                }
            })

            /* TODO Step 9: Make sure to use safe nullability markers like '?' when you access the fragment
                as it can be null at any point depending on the ad state */
            /* TODO Step 10: If you wanna destroy the fragment (the ad) for whatever reason, just make a transaction
                where you destroy the fragment from the instance 'adfragment' we have..   */



            /* TODO Step 11: One last thing, if you wanna change the default queries used by our default ad network
                API endpoint, then you can access defaultApiQueries in the ad builder and change their values before
                you fully build the ad.
                For example, adBuilder.defaultApiQueries.BIDFLOOR = "0.2"
             */
        }


        //        Thread.setDefaultUncaughtExceptionHandler {_,paramThrowable ->
//            Log.e(
//                "Error ${Thread.currentThread().stackTrace[2]}",
//                paramThrowable.message.toString()
//            )
//            paramThrowable.printStackTrace()
//        }

    }


}