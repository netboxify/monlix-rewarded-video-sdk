package com.netboxify.monlix_rewardedvideos_sdk

import android.graphics.Color
import com.netboxify.monlix_rewardedvideos_sdk.AdConstants.IMPRESSION_MODE_ON_FIRST_FRAME
import com.netboxify.monlix_rewardedvideos_sdk.AdConstants.IMPRESSION_MODE_THREE_SECONDS
import com.netboxify.monlix_rewardedvideos_sdk.AdConstants.IMPRESSION_MODE_UPON_FULL_WATCH

/**
 *  The class responsible for holding ad configuration. A default instance will be passed if the user
 * does not pass any customized instance of it.
 */
class RewardedAdsConfiguration {
    /** The API Endpoint used by the library, you can customize this value if you want to fetch
     * a VAST ad tag from a custom API endpoint of your choice. Then, you can ignore passing the three
     * parameters 'userId', 'zoneId' and 'publisherId' in the [build()] method. */
    private var apiEndpoint = ""
    /** Timeout used in each HTTP request call when fetching an ad. The default is 10 seconds.
     * If the request is timed out, the ad fragment will be removed.*/
    var httpTimeout: Long = 10L
        set(timeout) {
            field = if (timeout > 0L) {
                timeout
            } else {
                10L
            }
        }
    /** Use this to hide the mute button which is displayed by default. */
    val mutebutton = true

    /** This method is important and is used to select which mode the library uses to fire
     * an impression tracking. For example, [IMPRESSION_MODE_ON_FIRST_FRAME] means the impression URL
     * will be fired and called once the first frame of the ad is shown, which is the default.
     *
     * [IMPRESSION_MODE_ON_FIRST_FRAME] Registers an impression when the first frame is rendered.
     *
     * [IMPRESSION_MODE_UPON_FULL_WATCH] Registers an impression upon watching the full ad.
     *
     * [IMPRESSION_MODE_THREE_SECONDS] Registers an impression in 3 seconds.
     * @see AdConstants for more parameters.
     */
    val impressionMode: Int = IMPRESSION_MODE_ON_FIRST_FRAME
//        set(mode) {
//            field = if (mode in (0..2)) {
//                mode
//            } else {
//                /* Set to default if the wrong impression is selected */
//                0
//            }
//        }

    /** Changes the way the player chooses mediafile qualities based on
     * their bitrate and network connectivity.
     *
     * [Default] : Choosing smartly based on bandwidth.
     *
     * [Remark] : This may cause a 2-sec delay before playing ad.
     * You can change that behavior by changing the mode here.
     *
     * [Example] : changeBitrateChoosingMode(BITRATE_CHOOSING_MODE_LOWEST_RES) will load
     * the lowest quality mediafile available in the VAST, which is good for performance. */
    var bitrateChoosingMode: Int = AdConstants.BITRATE_CHOOSING_MODE_SMART
        set(mode) {
            field = if (mode in (0..2)) mode else bitrateChoosingMode
        }



    /** Indicates the orientation mode of the ad itself and whether the player should adapt it to
     * the screen or the activity's orientation. In usual cases, you'll need to set this to
     * [AdConstants.ORIENTATION_AD_DEPENDING], which rotates the ad depending on its resolution
     * and the screen's orientation. This will result in a seamless and efficient user experience.
     *
     * Available parameters:
     *
     * @param mode it can be either [AdConstants.ORIENTATION_HOST_ACTIVITY_DEPENDING] or [AdConstants.ORIENTATION_AD_DEPENDING]
     * @see AdConstants
     */
    var orientationMode: Int = AdConstants.ORIENTATION_AD_DEPENDING
        set(mode) {
            field = if (mode == 1 || mode == 0) mode else orientationMode
        }


    /** Represents the video/picture scaling mode of the ad. VAST ad-tags may or may not contain
     * information on how to scale up the ads. Therefore, you can set it manually here.
     *
     * However, if the VAST tag contains for example 'maintainAspectRatio' attributes, then this option
     * will be ignored and [AdConstants.SCALING_FIT] will be selected by default even if you choose to
     * choose [AdConstants.SCALING_FILL]
     *
     * [AdConstants.SCALING_FIT] refers to scaling the video maintaining the aspect ratio, this may leave
     * margins on one of the edges (horizontally or vertically) when the aspect ratio of the ad video does
     * not match the aspect ratio of the device itself.
     *
     * [AdConstants.SCALING_FILL] refers to scaling the video until it fills all the screen, leaving no margins
     * or void in the edges. However, this may be a bad option to choose in case the ad video's aspect ratio
     * is way too different from the device screen's aspect ratio.
     */
    var scalingMode = AdConstants.SCALING_FIT
        set(mode) {
            field = if (mode == 1 || mode == 0) mode else orientationMode
        }

    /** Indicates whether the countdown text should be displayed or not. In general, this is not needed, there is
     * a progress circle that already speaks volumes about the duration of the ads. But if you choose to show the
     * countdown text. Then set this to true.
     *
     * You can also customize the countdown text color, text size, and the text caption itself.
     * @see [countDownColor]
     * @see [countDownTextSize]
     * @see [countDownTextCaption]
     */
    val showCountdown = true

    /** Refers to the color of the countdown text, it is white by default.
     *
     * @see [countDownTextCaption]
     * @see [countDownTextSize]
     * @see [showCountdown]**/
    val countDownColor: Int = Color.WHITE

    /** Refers to the countdown text caption. It is by default :'The Rewarded Ad will close within:'.
     *
     * The library will then show the text with the amount of seconds left for the ad to finish.
     * @see [countDownColor]
     * @see [countDownTextSize]
     * @see [showCountdown]
     */
    val countDownTextCaption = "The Rewarded Ad will close within:"

    /** Refers to the size of the countdown text caption. It is 14sp by default.
     * @see [countDownColor]
     * @see [countDownTextCaption]
     * @see [showCountdown]
     */
    var countDownTextSize: Float = 14f
        set(size) {
            field = if (size>3f) size else countDownTextSize
        }


    /** Indicates whether to show the progress circle or not. Setting this to false will render it invisible.
     *
     * It is TRUE by default. For now, this option cannot be modified.
     * @see progresscircleColor**/
    val showProgressCircle = true

    /** Indicates the color of the progress circle. It is [Color.WHITE] by default. */
    var progresscircleColor: Int = Color.WHITE


    /** Refers to the visibility of the 'close' Button which is on the top right corner. It is TRUE by default,
     * which means it is visible unless you specify otherwise. For now, it's not modifiable.
     */
    val exitButton = true

    /** Refers to whether a user can click on the ad or not. Setting this to 'false' means that the user
     * will not be redirected to any url upon clicking, and the clicking events will not work.
     * For now, it is unmodifiable.
     */
    val clicking = true


    /** Refers the procedure to be taken if the ad fails to load. Usually, you'd want to set this to
     * [AdConstants.ON_FAILURE_CLOSE_AD].
     *
     * [AdConstants.ON_FAILURE_RETRY_THRICE] The SDK will try to load the ad three times at maximum.
     * [AdConstants.ON_FAILURE_RETRY] The library will keep trying to load the ad permanently.
     * [AdConstants.ON_FAILURE_RETRY_THRICE] The library will try to load the ad only three times.
     */
    var onfailureprotocol = AdConstants.ON_FAILURE_CLOSE_AD //Recommended option


    /** This indicates the video length needed to be first buffered in order to start playback.
     * Usually, you don't wanna mess with this as it may create bugs. However, you can decrease this
     * to start playback faster, or increase this for users who have excellent network conditions
     * in order to prevent video stuttering. But not all users have great bandwidths, this is set
     * to 1500ms by default.
     *
     * Lowest value allowed: 200 (milliseconds)
     */
    var minimumbuffering = 1500 //Minimum Video length to be buffered in order to start playback
        set(buffer) {
            field = if (buffer>200) buffer else minimumbuffering
        }


    /** Indicates whether the ad should be put on repeat upon complete. ON By default. **/
    var repeatmode = true

    /** Use this to enable/disable debugging messages. Disabled by default.
     *
     * Please note that all debug output will be filtered onto 'Error' level, that doesn't mean
     * that every debug output line defines an error, but this is to make it easier for devs to
     * debug the library more comfortably. **/
    var debugging = false

}
