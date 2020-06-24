package com.hossam.hasanin.watchittogeter.di

import com.hossam.hasanin.watchittogeter.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class UiBuilderModule {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity
}