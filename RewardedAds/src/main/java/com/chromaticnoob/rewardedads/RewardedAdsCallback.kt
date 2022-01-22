package com.chromaticnoob.rewardedads

/** Please note that the library already handles the callbacks and registers
 * all events. Only use these callback event methods to place your own methods
 *
 * This is the callback interface object used in order to receive callbacks.
 *
 * You can use the extended method in our RewardedAdsBuilder to set a callback listener this way:
 *
 *     val adBuilder = [RewardedAdsBuilder()]
 *
 *     adBuilder.setListener(object: RewardedAdsCallback { override the methods here }) */

interface RewardedAdsCallback {
    /** Indicates that the ad has been loaded into the player successfully and is now buffering **/
    fun adLoaded()

    /** Indicates that the ad has now started playing **/
    fun adStarted()

    /** Indicates that the ad has now been fully watched **/
    fun adWatched()

    /** Indicates that the ad has been clicked. **/
    fun adClicked()

    /** Indicates that the ad is closed
     *
     * @param afterSec How many seconds remaining from the video prior to closing it.
     * @param manually Whether the ad has been manually closed (by the user) or by the system due to
     * the unavaibility of the add for example.
     */
    fun adClosed(remainingSec: Long, manually: Boolean)

    /** Indicates that there is no ad found inside the VAST tag **/
    fun noAdAvailable()

    /** Indicates that there was an issue loading the ad **/
    fun onAdFetchFailed(error: String)


}