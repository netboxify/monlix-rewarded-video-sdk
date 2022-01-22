package com.chromaticnoob.rewardedads.networkutils

import android.net.TrafficStats

/**
 * This is used as a helper class to retrieve the current bandwith (after starting to load a video)
 * It will return the average network speed over a specific period of time used in the method's constructor
 * Then, the network bandwith returned will be compared to the available different bitrates for media files
 * in the ad, the closest bitrate will be chosen and its corresponding media file will be loaded.
 */
class TrafficUtils{
    companion object{
        fun getNetworkSpeed(msLong: Long): Long {
            var result = 0.0
            val speedsList = mutableListOf<Long>()
            val waitPeriod = 10L
            for (i in (0..msLong/waitPeriod)) {
                val speed = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()
                speedsList.add(speed)
                try {
                    Thread.sleep(waitPeriod)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            result = speedsList.toLongArray().average()

            /* Now we return the network speed in a bit unit (to compare it with video bitrate) */
            return ((result*8.0)/1000000.0).toLong()

        }
    }
}