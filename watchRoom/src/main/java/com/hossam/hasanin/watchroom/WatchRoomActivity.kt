package com.hossam.hasanin.watchroom

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.hossam.hasanin.base.models.WatchRoom
import com.hossam.hasanin.base.navigationController.NavigationManager
import com.hossam.hasanin.watchroom.groupRoom.GroupViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_watch_room.*
import javax.inject.Inject

@AndroidEntryPoint
class WatchRoomActivity : AppCompatActivity() {
    lateinit var room: WatchRoom
    var goTo: Int = 0
    private val viewModel by viewModels<WatchRoomViewModel>()
    @Inject lateinit var navigationManager: NavigationManager
    lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_room)

        room = intent?.extras!!.getParcelable("room")!!
        goTo = intent?.extras!!.getInt("goTo" , 0)

        FirebaseMessaging.getInstance().subscribeToTopic(room.id)

        disposable = viewModel.leftSuccessed.observeOn(AndroidSchedulers.mainThread()).subscribe {
            navigationManager.navigateTo(NavigationManager.MAIN , Bundle() , this)
        }

        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { controller, destination, arguments ->
            if(destination.id == R.id.playFragment) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }
        }

    }

    private fun popUpLeavingConfermation(){
        val alertDialog = AlertDialog.Builder(this)
        val ad = alertDialog.setMessage("Are you sure you wanna leave ?")
            .setPositiveButton("yes") { dialogInterface: DialogInterface, i: Int ->
                viewModel.leave(room.id)
            }.setNegativeButton("No"){
                    dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }.show()
    }

    override fun onBackPressed() {
        popUpLeavingConfermation()
    }

    override fun onResume() {
        super.onResume()
        if (goTo != 0){
            findNavController(R.id.nav_host_fragment).navigate(goTo)
        }
    }

    override fun onDestroy() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(room.id)
        //viewModel.leaveTheRoomUseCase(room.id)
        disposable.dispose()
        super.onDestroy()
    }

}