package com.senyor_o.firebasechat.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.senyor_o.firebasechat.R

class MyFirebaseMessagingService: FirebaseMessagingService() {

    private val channelId = "firebasechat_channel"
    val channelName = "MyNotification"
    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationBuilder :NotificationCompat.Builder

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            getSystemService(NotificationManager::class.java).createNotificationChannel(notificationChannel)
        }
        notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
        NotificationManagerCompat.from(this).notify(1, notificationBuilder.build())
    }
}