package com.chromaticnoob.rewardedads

import androidx.appcompat.app.AppCompatActivity
import com.chromaticnoob.rewardedads.ui.AdFragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder

/****************************************************************************************************
 * This is the main class responsible for building the ad. Instatiate this class, configure your ad,
 * then call 'build()'. The ad is loaded using a building pattern, which means, you declare the
 * method [build()] in order to build an instance of the ad class. After you successfully obtain
 * an instance of the ad using the building pattern. You can declare [launchAd()] to display the ad.
 *
 * Please note that you can customize the ad options while building it. All the ad settings/options
 * should be defined before declaring [building()], otherwise the Ad's behavior will be unexpected.
 * To configure an ad, use the method [configure()] to obtain a configuration class instance.
 * When you're done, insert your instance into the building method like this [build(yourConfiguration)].
 *
 * If you decide not to use any option customization, just call [build()] directly. The library will
 * then go with the default configuration. If
 ***************************************************************************************************/
class RewardedAdsBuilder {
    /** Library-related toolkit variables **/
    val gson: Gson = GsonBuilder().create()

    /** The queries that are used to fetch ads from our ad network .**/
    private val BASE_URL = "http://hbs.ams.rapidcodelab.com/rewarded-video/item.xml"
    private val APP_ID = "superapp"
    private val APP_BUNDLE = "com.rapidcodelab.demoapp"
    private val REWARD_URL = "http://appurl.com/reward&reward_type=health"
    private val REWARD_VALUE = "10"
    private val BIDFLOOR = "0.1"

    private var builtAdFragment: AdFragment? = null

    /** The most important method to call. Call this method after you're done having your custom
     * ad configuration. Please note that the 'activity'and 'fragmentContainer' parameters
     * are mandatory.
     *
     * @param activity The activity that is going to host the ad fragment. Pass 'this' if you're
     * instantiating the ad inside an activity.
     *
     * @param fragmentContainer The ID of the view that is going to contain the fragment,
     * preferably a FragmentContainer or a FrameLayout.
     *
     * @param configuration If you have instantiated a [RewardedAdsConfiguration] instance, pass it here.
     *
     * @param customVast If you want to skip getting VAST from network, pass your custom VAST here.
     *
     * @param userid If no custom API endpoint is used, specify your userId.
     * @param publisherid If no custom API endpoint is used, specify your publisherId.
     * @param zoneid If no custom API endpoint is used, specify your zoneid.
     *
     */
    fun build(
        hostActivity: AppCompatActivity,
        fragmentContainer: Int,
        customAdConfiguration: RewardedAdsConfiguration = configure(),
        customVast: String = "",
        userid: String = "",
        publisherid: String = "",
        zoneid: String = "",
    ) {
        if (userid != "" && publisherid != "" && zoneid != "") {
            customAdConfiguration.apiEndpoint = BASE_URL +
                    "?user_id=$userid" +
                    "&app_id=$APP_ID" +
                    "&publisher_id=$publisherid" +
                    "&zone_id=$zoneid" +
                    "&app_bundle=$APP_BUNDLE" +
                    "&reward_url=$REWARD_URL" +
                    "&reward_value=$REWARD_VALUE" +
                    "&bidfloor=$BIDFLOOR"
        }

        val adConfigMap: MutableMap<String, Any?> = mutableMapOf()
        adConfigMap["apiendpoint"] = customAdConfiguration.apiEndpoint
        adConfigMap["httptimeout"] = customAdConfiguration.httpTimeout
        adConfigMap["mutebutton"] = customAdConfiguration.mutebutton
        adConfigMap["impressionmode"] = customAdConfiguration.impressionMode
        adConfigMap["bitratemode"] = customAdConfiguration.bitrateChoosingMode
        adConfigMap["orientationmode"] = customAdConfiguration.orientationMode
        adConfigMap["debugging"] = customAdConfiguration.debugging
        adConfigMap["scalingmode"] = customAdConfiguration.scalingMode
        adConfigMap["showcountdown"] = customAdConfiguration.showCountdown
        adConfigMap["countdowntextsize"] = customAdConfiguration.countDownTextSize
        adConfigMap["countdowntextcolor"] = customAdConfiguration.countDownColor
        adConfigMap["countdowntextcaption"] = customAdConfiguration.countDownTextCaption
        adConfigMap["showprogresscircle"] = customAdConfiguration.showProgressCircle
        adConfigMap["progresscirclecolor"] = customAdConfiguration.progresscircleColor
        adConfigMap["exitbutton"] = customAdConfiguration.exitButton
        adConfigMap["clicking"] = customAdConfiguration.clicking
        adConfigMap["repeatmode"] = customAdConfiguration.repeatmode
        adConfigMap["minimumbuffering"] = customAdConfiguration.minimumbuffering
        if (customVast != "") {
            adConfigMap["vast"] = customVast
        }
        val adJson: String = gson.toJson(adConfigMap)

        builtAdFragment = AdFragment.newInstance(adJson = adJson)

        hostActivity.runOnUiThread {
            hostActivity.supportFragmentManager.beginTransaction()
                .add(fragmentContainer, builtAdFragment!!, "rewarded_ad")
                .commit()

        }


    }

    /** This will let you obtain a configuration instance which you can customize and then insert
     * into the building pattern as a parameter for [build] method. Please note that you cannot
     * configure the ad after building it, as that will result in unexpected behavior
     * @see [RewardedAdsConfiguration] */
    fun configure(): RewardedAdsConfiguration {
        return RewardedAdsConfiguration()
    }

    /****************************************************************************************
     * Use this method in order to retrieve the fragment instance that is being used at the time
     * of calling this. The library is the only responsible unit of nullifying or defining the ad fragment.
     * This will help you access the fragment directly for your own purposes but make sure
     * that this is highly disencouraged, since the fragment can be null at any point.
     *
     * Please note that this only works during the period the ad is shown and also when there
     * is only one ad fragment shown.
     * @param activity The host activity that is hosting the ad fragment.
     ****************************************************************************************/
    fun getAdFragment(): AdFragment? {
        return builtAdFragment
    }

    fun waitForFragment(timeoutMs: Long = 1000000): AdFragment? {
        val time = System.currentTimeMillis()
        var updatedtime = time
        while (builtAdFragment == null) {
            updatedtime = System.currentTimeMillis()
            if (updatedtime > (time + timeoutMs)) {
                return null
            }
        }
        return getAdFragment()
    }
}
