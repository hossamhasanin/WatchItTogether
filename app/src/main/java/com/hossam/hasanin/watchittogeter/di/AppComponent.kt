package com.hossam.hasanin.watchittogeter.di

import android.app.Application
import com.hossam.hasanin.watchittogeter.BaseApplication
import com.hossam.hasanin.watchittogeter.di.viewModelProviders.ViewModelFactoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        UiBuilderModule::class,
        ViewModelFactoryModule::class,
        AppModule::class
    ]
)
interface AppComponent : AndroidInjector<BaseApplication> {

    @Component.Factory
    interface Factory {
        fun application(@BindsInstance application: Application?): AppComponent
    }
}