package com.sharkoguess.app

import com.sharkoguess.app.BuildConfig
import com.sharkoguess.data.applemusic.TokenProvider
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SharkoGuessApp : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenProvider.developerToken = BuildConfig.APPLE_MUSIC_DEV_TOKEN
        TokenProvider.storefront = BuildConfig.APPLE_STOREFRONT
    }
}
