package com.hossam.hasanin.watchittogeter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import java.util.*
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hossam.hasanin.base.externals.NOTIFICATION_CHANALE_ID
import javax.inject.Inject
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat
import com.hossam.hasanin.watchroom.WatchRoomActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    lateinit var notificationManager:NotificationManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Setting up Notification channels for android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels()
        }

        val notificationId = Random().nextInt(60000)

        Log.v("Notification", remoteMessage.notification!!.title)

        val defaultSoundUri: Uri? =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Create an Intent for the activity you want to start
        val resultIntent = Intent(this, WatchRoomActivity::class.java).apply {
            val bundle = Bundle()
            bundle.putString("roomId" , remoteMessage.data["room_id"])
//            bundle.putString("videoUrl" , remoteMessage.data["room_video_url"])
            bundle.putBoolean("leader" , false)
            putExtras(bundle)
        }
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationBuilder: Builder =
            Builder(
                this,
                NOTIFICATION_CHANALE_ID.toString()
            )
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(remoteMessage.notification!!.title) //the "title" value you sent in your notification
                .setContentText(remoteMessage.notification!!.body) //ditto
                .setContentIntent(resultPendingIntent)
                .setSound(defaultSoundUri)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, notificationBuilder.build())
        }
        Log.v("Notification" , "noti")

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels() {
        val adminChannelName: CharSequence = getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = getString(R.string.notifications_admin_channel_description)
        val adminChannel = NotificationChannel(
            NOTIFICATION_CHANALE_ID.toString() ,
            adminChannelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel)
        }
    }

}