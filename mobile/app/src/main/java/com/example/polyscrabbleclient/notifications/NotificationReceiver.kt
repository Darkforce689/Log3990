package com.example.polyscrabbleclient.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.polyscrabbleclient.MainActivity
import com.example.polyscrabbleclient.R
import com.example.polyscrabbleclient.ui.theme.RemainderContentNotificationFR
import com.example.polyscrabbleclient.ui.theme.RemainderTitleNotificationFR

enum class NotificationType {
    Remainder
}

class NotificationReceiver : BroadcastReceiver() {

    private val channelID = "NOTIFICATION_CHANNEL_ID"
    private val channelName = "NOTIFICATION_CHANNEL_NAME"
    private val channelImportance = NotificationManager.IMPORTANCE_DEFAULT

    override fun onReceive(context: Context, intent: Intent) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val newIntent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_IMMUTABLE)
        createNotificationChannel(context)
        sendRemainder(context, pendingIntent)
    }

    private fun sendRemainder(
        context: Context,
        pendingIntent: PendingIntent,
        title: String = RemainderTitleNotificationFR,
        content: String = RemainderContentNotificationFR(
            NotificationDelay * (remainderID + 1)
        ),
    ) {
        remainderID++
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.favicon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
        with(NotificationManagerCompat.from(context)) {
            notify(NotificationType.Remainder.ordinal, notification.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        val notificationChannel = NotificationChannel(
            channelID,
            channelName,
            channelImportance
        )
        val notificationManager =
            context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    companion object {
        private var remainderID = 0
        fun resetRemainderId() {
            remainderID = 0
        }
    }
}
