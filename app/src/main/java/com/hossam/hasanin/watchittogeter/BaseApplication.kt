package com.hossam.hasanin.watchittogeter

import android.app.Application
import android.content.Intent
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.hossam.hasanin.authentication.AuthenticationActivity
import com.hossam.hasanin.base.navigationController.NavigationManager
import com.hossam.hasanin.base.models.User
import com.hossam.hasanin.watchroom.WatchRoomActivity
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application() {
    @Inject lateinit var navigationManager: NavigationManager
    val compositeDisposable = CompositeDisposable()
    override fun onCreate() {
        super.onCreate()

        val options = FirebaseOptions.Builder()
            .setProjectId("watchittogether-2b467")
            .setApplicationId("1:380110250463:android:32cacfc37c46a5c5f5646a")
            .setApiKey("AIzaSyDqwV_xEjlGg4k6xCDeJ3bl5ocPmTDbb9I")
            .build()

        FirebaseApp.initializeApp(applicationContext , options , "watchittogether-2b467")

        User.current = null
        val disposable = navigationManager.navigationStream().observeOn(AndroidSchedulers.mainThread()).subscribe { navModel ->
            val activity = when(navModel.destination){
                NavigationManager.MAIN -> {
                    MainActivity::class.java
                }

                NavigationManager.WATCH_ROOM -> {
                    WatchRoomActivity::class.java
                }

                NavigationManager.AUTH -> {
                    AuthenticationActivity::class.java
                }
                else -> {
                    throw Exception("Not matched activity")
                }
            }
            navModel.activity.startActivity(Intent(navModel.activity , activity).putExtras(navModel.data))
            navModel.activity.finish()
        }

        compositeDisposable.add(disposable)
    }

}