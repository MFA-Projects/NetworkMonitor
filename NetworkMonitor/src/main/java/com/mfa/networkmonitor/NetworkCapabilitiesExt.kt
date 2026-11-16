package com.mfa.networkmonitor


import android.net.NetworkCapabilities
import android.os.Build

fun NetworkCapabilities.isEligibleInternet(): Boolean {
    return hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

fun NetworkCapabilities.hasWifiTransport(): Boolean {
    return hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
}

fun NetworkCapabilities.hasCellularTransport(): Boolean {
    return hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
}

fun NetworkCapabilities.isSignalStrong(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val signal = signalStrength
        signal == NetworkCapabilities.SIGNAL_STRENGTH_UNSPECIFIED || signal > -75
    } else {
        true
    }
}