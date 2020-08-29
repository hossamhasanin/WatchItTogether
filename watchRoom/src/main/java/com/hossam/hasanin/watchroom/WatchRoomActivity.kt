package com.hossam.hasanin.watchroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.hossam.hasanin.base.models.WatchRoom
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchRoomActivity : AppCompatActivity() {
    lateinit var room: WatchRoom
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_room)

        room = intent?.extras!!.getParcelable("room")!!
        FirebaseMessaging.getInstance().subscribeToTopic(room.id)


    }

    override fun onDestroy() {
        super.onDestroy()
        FirebaseMessaging.getInstance().unsubscribeFromTopic(room.id)
    }

}