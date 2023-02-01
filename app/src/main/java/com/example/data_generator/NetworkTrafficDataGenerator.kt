package com.example.data_generator

import android.content.Context
import android.content.pm.PackageManager
import android.net.TrafficStats
import com.example.converters.ByteArrayToBinaryStringConverter
import com.example.utilities.EnvVariables
import kotlin.experimental.and

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

    fun networkTrafficVolume(): ByteArray {
        val stringBuilder = StringBuilder()

        val mobileBytesSent = TrafficStats.getMobileTxBytes()
        val mobileBytesReceived = TrafficStats.getMobileRxBytes()
        val mobilePacketsSent = TrafficStats.getMobileTxPackets()
        val mobilePacketsReceived = TrafficStats.getMobileRxPackets()
        val wifiBytesSent = TrafficStats.getTotalTxBytes() - mobileBytesSent
        val wifiBytesReceived = TrafficStats.getTotalRxBytes() - mobileBytesReceived
        val wifiPacketsSent = TrafficStats.getTotalTxPackets() - mobilePacketsSent
        val wifiPacketsReceived = TrafficStats.getTotalRxPackets() - mobilePacketsReceived

        stringBuilder.append("${mobileBytesSent - previousMobileBytesSent}${mobileBytesReceived - previousMobileBytesReceived}")
        stringBuilder.append("${mobilePacketsSent - previousMobilePacketsSent}${mobilePacketsReceived - previousMobilePacketsReceived}")
        stringBuilder.append("${wifiBytesSent - previousWifiBytesSent}${wifiBytesReceived - previousWifiBytesReceived}")
        stringBuilder.append("${wifiPacketsSent - previousWifiPacketsSent}${wifiPacketsReceived - previousWifiPacketsReceived}")

        previousMobileBytesSent = mobileBytesSent
        previousMobileBytesReceived = mobileBytesReceived
        previousMobilePacketsSent = mobilePacketsSent
        previousMobilePacketsReceived = mobilePacketsReceived
        previousWifiBytesSent = wifiBytesSent
        previousWifiBytesReceived = wifiBytesReceived
        previousWifiPacketsSent = wifiPacketsSent
        previousWifiPacketsReceived = wifiPacketsReceived

        return convertToByteArray(stringBuilder.toString())
    }

    private fun convertToByteArray(data: String): ByteArray {
        if (data.toLong() == 0L) {
            return ByteArray(0)
        }

        val dataToFloat = data.toLong()
        val modData = dataToFloat % EnvVariables.PRIME_FOR_MOD

        val byte = modData.toInt().toByte()

        return byteArrayOf(byte and 0xff.toByte())
    }
}