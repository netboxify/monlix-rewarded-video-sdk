package com.netboxify.monlix_rewardedvideos_sdk.vastutils

class Creative {
    var sequence: String = ""
    var creativeid: String? = ""
    var creativeadId: String? = ""
    var creativeType: String = "" //Linear supported only for now
    var universalid: String? = ""
    var duration: String = ""
    var videoClickThrough: String? = ""
    var videoClickTracking: String? = ""
    var mediaList: MutableList<MediaFile> = mutableListOf()

    var trackingEvents: MutableMap<String, String> = mutableMapOf() //Event and corresponding url
    /** List of possible Tracking Events for VAST 2.0 :
     * @param creativeView called when the ad is first loaded ([IMPORTANT])
     * @param start called when ad actually starts playing ([IMPORTANT])
     * @param midpoint called when ad reaches half the total duration
     * @param firstQuartile called upon reaching 25% of the duration
     * @param thirdQuartile called upon reaching 75% of the duration
     * @param complete when ad is complete ([IMPORTANT])
     * @param mute when user mutes the ad ([IMPORTANT])
     * @param unmute when user unmutes the ad
     * @param pause when user pauses the ad ([IMPORTANT])
     * @param rewind when user rewinds the ad (Not allowed in this library)
     * @param resume when user resumes the ad
     * @param fullscreen when user sets the ad to fullscreen (already called everytime)
     * @param expand when user expands the size of the ad (not for phones)
     * @param collapse when user hides/collapses the ad (not for phones)
     * @param close when user closes the ad ([IMPORTANT])
     * @param acceptInvitation when user accepts invitation dialogue
     */
    fun trackingeventsJavaDoc() {}
}