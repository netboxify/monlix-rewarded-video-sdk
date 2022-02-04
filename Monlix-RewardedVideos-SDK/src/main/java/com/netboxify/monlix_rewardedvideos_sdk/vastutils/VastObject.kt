package com.netboxify.monlix_rewardedvideos_sdk.vastutils

class VastObject {
    var vastVersion: String = "2.0"
    var shortAdId: String = ""
    var adType: String = "" //Inline
    var adSystem: String = ""
    var adSystemVersion: String? = ""
    var adTitle: String = ""
    var error: String = ""
    var impressionList: MutableList<String> = mutableListOf()
    var creativeList: MutableList<Creative> = mutableListOf()

    /************************************************************************************
     * Currently, the library only supports VAST version 2.0 and most of its parameters
     * Features that are still unsupported but will be supported soon :
     * Multiple Creatives, Wrapper ads, NonLinear ads, Companion Ads
     ***********************************************************************************/

}