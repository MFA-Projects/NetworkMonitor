📡 Android Real-Time Network Monitor

A lightweight, reactive, and reusable Android library built with Kotlin Coroutines and StateFlow to observe real-time network connectivity states—including connection status, transport type (Wi-Fi/Cellular), and signal strength—without battery-draining polling loops.

✨ Features

100% Event-Driven: Powered by Android's ConnectivityManager.NetworkCallback. Zero manual timer loops or battery drain.

Validated Internet Check: Ensures the device actually has active internet access (NET_CAPABILITY_VALIDATED), preventing false positives when connected to Wi-Fi with no internet.

Signal Quality Detection: Automatically detects whether the active connection has a strong or weak signal (Android API 29+).

Transport Identification: Distinguishes between Wi-Fi and Cellular connections out of the box.

Lifecycle & Coroutine Friendly: Exposes a cold Flow<NetworkStatus> that easily converts into a StateFlow inside any ViewModel.

📦 Installation

Option 1: Via JitPack (Recommended for GitHub Repos)

Add JitPack to your root settings.gradle.kts (or build.gradle):

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}


[![](https://jitpack.io/v/MFA-Projects/NetworkMonitor.svg)](https://jitpack.io/#MFA-Projects/NetworkMonitor)
Add the dependency to your app module's build.gradle.kts:
dependencies {
implementation 'com.github.MFA-Projects:NetworkMonitor:Tag'
}


Option 2: Direct Drop-In

Simply copy the networkmonitor package (ConnectivityObserver.kt, NetworkStatus.kt, and NetworkCapabilitiesExt.kt) directly into your project's utility or core module.

🔐 Required Permissions

Make sure you have the following permission declared in your AndroidManifest.xml:

<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />


🚀 Usage

1. Expose as StateFlow in your ViewModel

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val connectivityObserver = ConnectivityObserver(application)

    val networkStatus: StateFlow<NetworkStatus> = connectivityObserver.observer
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NetworkStatus.Unavailable
        )
}


2. Observe in Jetpack Compose UI

@Composable
fun NetworkStatusBanner(viewModel: MainViewModel) {
    val status by viewModel.networkStatus.collectAsState()

    when (val current = status) {
        is NetworkStatus.Available -> {
            val connectionType = if (current.isWifi) "Wi-Fi" else "Cellular"
            val signalQuality = if (current.isSignalStrong) "Strong Signal 🟢" else "Weak Signal 🟡"
            Text(text = "Connected via $connectionType ($signalQuality)")
        }
        NetworkStatus.Losing -> {
            Text(text = "Connection is unstable... 🟠")
        }
        NetworkStatus.Lost, NetworkStatus.Unavailable -> {
            Text(text = "No Internet Connection 🔴")
        }
    }
}


📊 Network States Reference

State

Description

NetworkStatus.Available

Device is connected with validated internet access. Includes isWifi, isCellular, and isSignalStrong flags.

NetworkStatus.Losing

The network is about to be lost (e.g., walking out of Wi-Fi range).

NetworkStatus.Lost

The active network connection was just disconnected.

NetworkStatus.Unavailable

No active or validated internet connection is available.

📄 License

MIT License

Copyright (c) 2026 MFA-Projects

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
