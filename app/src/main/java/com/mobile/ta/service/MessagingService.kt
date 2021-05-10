package com.mobile.ta.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mobile.ta.R
import com.mobile.ta.repository.NotificationRepository
import com.mobile.ta.repository.impl.NotificationRepositoryImpl
import com.mobile.ta.ui.main.MainActivity
import com.mobile.ta.utils.isNotNullOrBlank
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var notificationRepository: NotificationRepository

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title
        val message = remoteMessage.notification?.body
        if (message != null && title != null) {
            sendPushNotification(title, message)
        }
    }

    override fun onNewToken(token: String) {
        sendTokenToFirestore(token)
    }

    private fun sendPushNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE,
            intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val channelId = getString(R.string.general_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.mlearn_logo)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            getString(R.string.general_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun sendTokenToFirestore(token: String) {
        //TODO: Use logged in user ID
        GlobalScope.launch(Dispatchers.IO) {
            notificationRepository.sendNotificationToken("l1CLTummIoarBY3Wb3FY", token)
        }
    }

    companion object {
        const val REQUEST_CODE = 0
        const val NOTIFICATION_ID = 0
    }
}