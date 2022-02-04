package com.netboxify.monlix_rewardedvideos_sdk

import android.util.Log
import androidx.lifecycle.ViewModel
import com.netboxify.monlix_rewardedvideos_sdk.vastutils.VastObject
import okhttp3.*
import okio.IOException
import java.util.concurrent.TimeUnit

/**************************************************************************************************
 * This plays as the delegate for the library to fetch HTTP requests as well as hold all the necessary
 * data for the ad fragment even after the fragment loses its data state (For example, screen orientation).
 * This pertains to a healthy MVVM-architecture and therefore enhances the performance of the library
 * by miles. The ViewModel holds all the ad configuration and information necessary to continue playing
 * the ad even after the ad fragment is lost or recreated. The class itself is internal in order to
 * prevent library users from accessing this class directly. As that is both useless and dangerous.
 *************************************************************************************************/
internal class AdViewModel(): ViewModel() {

    var working: Boolean = false
    var adConfig: RewardedAdsConfiguration = RewardedAdsConfiguration()
    var adVastObject: VastObject = VastObject()

    /* Using internal keyword to make sure devs don't misuse this method */
     fun sendImpression() {
        Thread {
            val client = OkHttpClient().newBuilder()
                .connectTimeout(adConfig.httpTimeout, TimeUnit.SECONDS)
                .readTimeout(adConfig.httpTimeout, TimeUnit.SECONDS)
                .writeTimeout(adConfig.httpTimeout, TimeUnit.SECONDS)
                .build()
            val impressionList = adVastObject.impressionList
            for (impression in impressionList) {
                val request: Request = Request.Builder()
                    .url(impression)
                    .method("GET", null)
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: java.io.IOException) {
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                    }
                })
            }
        }.start()
    }

    /* Unlike impressions (where there exists more than 1 sometimes), this one is used to GET
     * any URL related to TrackingEvents or ClickEvents */
     fun sendTrack(url: String) {
        Thread {
            try {
                val client = OkHttpClient().newBuilder()
                    .connectTimeout(adConfig.httpTimeout, TimeUnit.SECONDS)
                    .readTimeout(adConfig.httpTimeout, TimeUnit.SECONDS)
                    .writeTimeout(adConfig.httpTimeout, TimeUnit.SECONDS)
                    .build()
                val request: Request = Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: java.io.IOException) {
                        logThis("Failed to send a tracking event due to an HTTP exception. URL: $url")
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        logThis("TrackUrl requested successfully. URL: $url")
                    }
                })
            } catch(e: IllegalArgumentException) {}
        }.start()
    }

    /** Use this method only when you want to track a certain URL manually. In the real world,
     * this method really has no apparent use. I am leaving this as an open choice if you want to
     * use the integrated Okhttp client to make HTTP calls. */
    fun trackCustomUrl(url: String) {
        Thread {
            val client = OkHttpClient().newBuilder()
                .connectTimeout(adConfig.httpTimeout, TimeUnit.SECONDS)
                .readTimeout(adConfig.httpTimeout, TimeUnit.SECONDS)
                .writeTimeout(adConfig.httpTimeout, TimeUnit.SECONDS)
                .build()
            val request: Request = Request.Builder()
                .url(url)
                .method("GET", null)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    logThis("Failed to send a request to the custom URL due to an HTTP exception. URL: $url")
                }
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    logThis("Custom URL tracked successfully. URL: $url")
                }
            })
        }.start()
    }


    /* Debugger Function */
     fun logThis(msg: String) {
        /* All debug reports are sent to "Error" for easier readability, doesn't mean all messages
           are actually errors. This is my way of making it easier for devs who use this library */
        if (adConfig.debugging) {
            Log.e("RewardedAds", msg)
        }
    }

}