package com.netboxify.monlix_rewardedvideos_sdk
import androidx.fragment.app.Fragment
import com.netboxify.monlix_rewardedvideos_sdk.vastutils.VastObject
/********************************************************************************
 * Main class that is responsible for building the ad, it contains   *
 * It takes a @param FragmentActivity in order to host the ad fragment.         *
 * A FragmentActivity is a AppCompatActivity for example, You cannot insert     *
 * a normal non-AppCompat Activity into this property.                          *
 *******************************************************************************/

class RewardedAds( //Can only be ActivityCompat/AppCompatActivity
    val containerId: Int, //The container view id into which the ad fragment is hosted.
    val userId: String,
    val zoneId: String,
    val publisherId: String) {

    var ad = VastObject()
    var adfragment: Fragment? = null
    var adwatched = false
    /*******************************************************************************
     * Interface for the callbacks, they will work in synergy with VAST events     *
     *******************************************************************************/
//    private lateinit var adCallback: AdCallback
//
//    fun addCallbackListener(adcallback: AdCallback) {
//        this.adCallback = adcallback
////    }

    /*********************************************************************************************
     *            Here starts our configuration methods, used as options for customizing the ad
     *  Mostly, they're used to customize the UI and functionality of the ad handling, some others
     *  are used to help developers make more use of the library. Please note that the library
     *  uses its own set of default configuration and does not need any options to be set.
     *  Therefore, you can literally skip doing any configuration, the library will load the ad
     *  as per the default configuration. Also note that you can only configure the ad before
     *  loading it, or else the ad behavior will be very unexpected. @refer to DemoProject.
     **********************************************************************************************/
//
//
////How should the ad be scaled, should it be fit in XY directions (but leaves margins)
////Or mode=1 means it's scaled until it's filled.
//    fun setAdScalingMode(mode: Int) {
//        if (mode == 1 || mode == 0) {
//            scalingMode = mode
//        }
//    }
//
//    //This method enables countdown text throughout the duration of the ad.
//    fun showCountdownText(boolean: Boolean) { showCountdown = boolean }
//
//    //Customizes the countdown text color if enabled.
//    fun setCountdownTextColor(colorint: Int) { countDownColor = colorint }
//
//    //Shows progress circle (which is enabled by default) around the exit button.
//    fun showProgressCircle(boolean: Boolean) { progresscircle = boolean}
//
//    //Customizes the color of the progress circle. TODO
//    fun setProgressCircleColor(colorint: Int) { progresscircleColor = colorint }
//
//    //Hide exit button. (False by default)
//    fun hideExitButton(boolean: Boolean) { exitButton = !boolean }
//
//    //Disable click tracking (false by default).
//    fun disableClicking(boolean: Boolean) { clicking = !boolean }
//
//    //what to do on failure of fetching ad, retry forever or close ad ? TODO
//    fun onFailure(mode: Int) { if (mode == 1 || mode == 0) { onfailureprotocol = mode } }
//
//    //Duration of the video needed to be buffered before the ad starts out. TODO
////It needs to be short but not too short (to not cause problems for the player)
////but not too long either so it doesn't take forever to start playback.
//    fun customBufferingLength(long: Long) { minimumbuffering = long }
//
//
//    //Set repeat mode on, which will repeat ad video after it finishes. TODO
//    fun repeatMode(boolean: Boolean) { repeatmode = boolean }

}









