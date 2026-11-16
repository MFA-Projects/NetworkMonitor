package com.mfa.networkmonitor

sealed interface NetworkStatus {
    object Unavailable : NetworkStatus
    object Lost : NetworkStatus
    object Losing : NetworkStatus
    data class Available(
        val isWifi: Boolean,
        val isCellular: Boolean,
        val isSignalStrong: Boolean // true jika sinyal di atas threshold bagus
    ) : NetworkStatus
}