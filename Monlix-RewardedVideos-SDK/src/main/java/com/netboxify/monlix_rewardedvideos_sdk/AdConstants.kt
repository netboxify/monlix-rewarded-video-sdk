package com.netboxify.monlix_rewardedvideos_sdk

object AdConstants {

    /*****************************************************************************
     *  These are the default constants used when using configuration methods    *
    ******************************************************************************/
    /** Indicates registering the ad's impression upon rendering the first frame. **/
    const val IMPRESSION_MODE_ON_FIRST_FRAME = 0
    /** Indicates registering the ad's impression upon watching the full ad. **/
    const val IMPRESSION_MODE_UPON_FULL_WATCH = 1
    /** Indicates registering the ad's impression upon watching 3 seconds of the ad. **/
    const val IMPRESSION_MODE_THREE_SECONDS = 2

    /** Indicates choosing the ad's mediafile smartly depending on the network's performance.
     * Please note that this is just experimental and not recommended, as it may behave differently
     * on different devices. Please use either [BITRATE_CHOOSING_MODE_HIGHEST_RES] or
     * [BITRATE_CHOOSING_MODE_LOWEST_RES].**/
    const val BITRATE_CHOOSING_MODE_SMART = 0 //Tests Bandwith and adapts to it [EXPERIMENTAL!!]
    /** Indicates that the library chooses the lowest quality mediafile that is available **/
    const val BITRATE_CHOOSING_MODE_LOWEST_RES = 1
    /** Indicates that the library chooses the highest quality mediafile that is available **/
    const val BITRATE_CHOOSING_MODE_HIGHEST_RES = 2


    /** Indicates that the ad's orientation is not based on its own aspect ratio but rather on
     * the orientation of the activity that is hosting the ad fragment. This is not recommended
     * as it is bad for User Experience. **/
    const val ORIENTATION_HOST_ACTIVITY_DEPENDING = 0

    /** Indicates that the library will rotate the ad depending on its aspect ratio no matter what
     * the hosting activity's orientation is. This is the recommended mode. However, it does not
     * behave the same way across all devices. Only use this when the library is in stable versions.
     * The library SDK will exhibit a few calculations to set the orientation of the ad, and there
     * is always a chance that it will rotate the app to the wrong side. Therefore, if unsure, use
     * [ORIENTATION_HOST_ACTIVITY_DEPENDING] but the ad will not be rotated. */
    const val ORIENTATION_AD_DEPENDING = 1


    /** Fit the ad video frame to the screen. This will leave
     * margins and spaces in the edges if the ad video's aspect ratio does not match the device's
     * resolution. But it is recommended because it does not crop the ad.*/
    const val SCALING_FIT = 0

    /** Indicates that the video player should fill the content of the ad video to the borders in
     * order to avoid leaving margins and spaces in the edges. This is helpful for a full immersive
     * experience but in cases where the device resolution is too different from the ad video's
     * resolution, this may crop a lot from the content.
     *
     * [NOTE] : PLease note that this will be ignored if the VAST contains a maintainAspectRatio attribute.*/
    const val SCALING_FILL = 1

    /** [Not_yet_implemented] - Indicates that the player should retry playing ad permanently on failure **/
    const val ON_FAILURE_RETRY = 0
    /** Indicates that the player should close the ad on the first failure **/
    const val ON_FAILURE_CLOSE_AD = 1
    /** [Not_yet_implemented] - Indicates that the player should retry for 3 times, otherwise close ad. **/
    const val ON_FAILURE_RETRY_THRICE = 2

}