package com.example.polyscrabbleclient.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent

const val NotificationDelay = 5 // min
const val NotificationDelayMs = NotificationDelay * 60 * 1000L

enum class NotificationFrequency {
    Once,
    Repeating
}

object NotificationsEmitter {

    private val notificationFrequency = NotificationFrequency.Once

    fun stopDelayedRemainder(context: Context) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent !== null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun startDelayedRemainder(context: Context) {
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        NotificationReceiver.resetRemainderId()
        when (notificationFrequency) {
            NotificationFrequency.Once -> setOnce(alarmManager, pendingIntent)
            NotificationFrequency.Repeating -> setRepeating(alarmManager, pendingIntent)
        }
        setRepeating(alarmManager, pendingIntent)
    }

    private fun setRepeating(
        alarmManager: AlarmManager,
        pendingIntent: PendingIntent?
    ) {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + NotificationDelayMs,
            NotificationDelayMs,
            pendingIntent
        )
    }

    private fun setOnce(
        alarmManager: AlarmManager,
        pendingIntent: PendingIntent?
    ) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + NotificationDelayMs,
            pendingIntent
        )
    }

}
