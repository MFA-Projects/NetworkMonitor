package com.mfa.networkmonitor


import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class ConnectivityObserver(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val observer: Flow<NetworkStatus> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                emitStatus(capabilities)
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                // Dipanggil real-time misal saat kualitas sinyal berubah atau pindah access point
                emitStatus(networkCapabilities)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                trySend(NetworkStatus.Losing)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(NetworkStatus.Lost)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                trySend(NetworkStatus.Unavailable)
            }

            private fun emitStatus(capabilities: NetworkCapabilities?) {
                if (capabilities != null && capabilities.isEligibleInternet()) {
                    val isWifi = capabilities.hasWifiTransport()
                    val isCell = capabilities.hasCellularTransport()
                    val isStrong = capabilities.isSignalStrong()

                    trySend(
                        NetworkStatus.Available(
                            isWifi = isWifi,
                            isCellular = isCell,
                            isSignalStrong = isStrong
                        )
                    )
                } else {
                    trySend(NetworkStatus.Unavailable)
                }
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)


        if (!checkInitialConnection()) {
            trySend(NetworkStatus.Unavailable)
        }

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    private fun checkInitialConnection(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.isEligibleInternet()
    }
}