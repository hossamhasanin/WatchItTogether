package com.hossam.hasanin.watchittogeter

import android.app.Application
import com.hossam.hasanin.watchittogeter.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

open class BaseApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().application(applicationContext as Application?)
    }

    override fun onCreate() {
        super.onCreate()
    }
}