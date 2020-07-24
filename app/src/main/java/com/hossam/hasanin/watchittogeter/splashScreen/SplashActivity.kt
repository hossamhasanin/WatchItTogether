package com.hossam.hasanin.watchittogeter.splashScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.hossam.hasanin.authentication.AuthenticationActivity
import com.hossam.hasanin.base.navigationController.NavigationManager
import com.hossam.hasanin.watchittogeter.MainActivity
import com.hossam.hasanin.watchittogeter.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val viewModel by viewModels<SplashViewModel>()
    @Inject lateinit var navigationManager: NavigationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel.checkIfLoggedIn { loggedIn ->
            if (loggedIn){
                navigationManager.navigateTo(NavigationManager.MAIN , Bundle() , this)
            } else {
                navigationManager.navigateTo(NavigationManager.AUTH , Bundle() , this)
            }
        }

    }
}