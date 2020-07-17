package com.hossam.hasanin.watchittogeter

import android.app.Application
import com.hossam.hasanin.watchittogeter.models.User
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        User.current = null
    }
}