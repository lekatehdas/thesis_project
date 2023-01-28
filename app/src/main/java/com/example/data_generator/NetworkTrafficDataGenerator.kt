package com.example.data_generator

import android.content.Context
import android.content.pm.PackageManager
import android.net.TrafficStats

class NetworkTrafficDataGenerator(private val context: Context) {
    private var previousMobileBytesSent: Long = TrafficStats.getMobileTxBytes()
    private var previousMobileBytesReceived: Long = TrafficStats.getMobileRxBytes()
    private var previousMobilePacketsSent: Long = TrafficStats.getMobileTxPackets()
    private var previousMobilePacketsReceived: Long = TrafficStats.getMobileRxPackets()
    private var previousWifiBytesSent: Long =
        TrafficStats.getTotalTxBytes() - previousMobileBytesSent
    private var previousWifiBytesReceived: Long =
        TrafficStats.getTotalRxBytes() - previousMobileBytesReceived
    private var previousWifiPacketsSent: Long =
        TrafficStats.getTotalTxPackets() - previousMobilePacketsSent
    private var previousWifiPacketsReceived: Long =
        TrafficStats.getTotalRxPackets() - previousMobilePacketsReceived

    fun networkTrafficVolume(): String {
        val stringBuilder = StringBuilder()
        val mobileBytesSent = TrafficStats.getMobileTxBytes()
        val mobileBytesReceived = TrafficStats.getMobileRxBytes()
        val mobilePacketsSent = TrafficStats.getMobileTxPackets()
        val mobilePacketsReceived = TrafficStats.getMobileRxPackets()
        val wifiBytesSent = TrafficStats.getTotalTxBytes() - mobileBytesSent
        val wifiBytesReceived = TrafficStats.getTotalRxBytes() - mobileBytesReceived
        val wifiPacketsSent = TrafficStats.getTotalTxPackets() - mobilePacketsSent
        val wifiPacketsReceived = TrafficStats.getTotalRxPackets() - mobilePacketsReceived

        if ((mobileBytesSent - previousMobileBytesSent) != 0L || (mobileBytesReceived - previousMobileBytesReceived) != 0L) {
            stringBuilder.append("${mobileBytesSent - previousMobileBytesSent}${mobileBytesReceived - previousMobileBytesReceived}")
        }
        if ((mobilePacketsSent - previousMobilePacketsSent) != 0L || (mobilePacketsReceived - previousMobilePacketsReceived) != 0L) {
            stringBuilder.append("${mobilePacketsSent - previousMobilePacketsSent}${mobilePacketsReceived - previousMobilePacketsReceived}")
        }
        if ((wifiBytesSent - previousWifiBytesSent) != 0L || (wifiBytesReceived - previousWifiBytesReceived) != 0L) {
            stringBuilder.append("${wifiBytesSent - previousWifiBytesSent}${wifiBytesReceived - previousWifiBytesReceived}")
        }
        if ((wifiPacketsSent - previousWifiPacketsSent) != 0L || (wifiPacketsReceived - previousWifiPacketsReceived) != 0L) {
            stringBuilder.append("${wifiPacketsSent - previousWifiPacketsSent}${wifiPacketsReceived - previousWifiPacketsReceived}")
        }

        previousMobileBytesSent = mobileBytesSent
        previousMobileBytesReceived = mobileBytesReceived
        previousMobilePacketsSent = mobilePacketsSent
        previousMobilePacketsReceived = mobilePacketsReceived
        previousWifiBytesSent = wifiBytesSent
        previousWifiBytesReceived = wifiBytesReceived
        previousWifiPacketsSent = wifiPacketsSent
        previousWifiPacketsReceived = wifiPacketsReceived

        return stringBuilder.toString()
    }

    fun amplifyNetworkData(data: String): String {
        if (data.isEmpty()) {
            return ""
        }

        val numericData = data.toDouble()
        val floatData: Double = numericData/numberOfInstalledAppsOnDevice()

        return getFractionalPart(floatData)
    }

    private fun numberOfInstalledAppsOnDevice(): Int {
        val flags = PackageManager.GET_META_DATA
        val numberOfApps = context.packageManager.getInstalledPackages(flags).size

        if (numberOfApps == 0) {
            return 1
        }

        return numberOfApps
    }

    private fun getFractionalPart(num: Double): String {
        return num.toString().split(".")[1]

    }
}