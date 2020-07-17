package com.hossam.hasanin.watchittogeter

import android.app.Application
import com.hossam.hasanin.watchittogeter.models.User
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
<<<<<<< HEAD

=======
    override fun onCreate() {
        super.onCreate()
        User.current = null
    }
>>>>>>> 0b048c95498496807a9f6433571808c86d3210b2
}