package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat
import com.udacity.Utils.NOTIFICATION_ID

object Utils {
     val NOTIFICATION_ID = 0
}

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {

    if (SDK_INT >= O) {
        val name = applicationContext.getString(R.string.notification_channel_name)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(
            applicationContext.getString(R.string.notification_channel_id),
            name,
            importance
        )

        // Register the channel with the system
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)

    notify(NOTIFICATION_ID, builder.build())


}