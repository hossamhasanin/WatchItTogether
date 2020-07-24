package com.hossam.hasanin.watchroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WatchRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_room)

        val roomId = intent?.extras!!.getString("roomId")
        Log.v("lolo", roomId)

    }
}