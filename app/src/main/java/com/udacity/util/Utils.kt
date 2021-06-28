package com.udacity.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat
import com.udacity.R
import com.udacity.ui.DetailActivity
import com.udacity.util.Constants.NOTIFICATION_ID


fun NotificationManager.sendNotification(fileName : String, messageBody: String, status : DownloadTracker, applicationContext: Context) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(Constants.FILE_NAME, fileName)
    contentIntent.putExtra(Constants.STATUS, status.name)

    val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

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
            .setColor(applicationContext.resources.getColor(R.color.white))
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(messageBody)
            //.setContentIntent(contentPendingIntent)
            .addAction(
                R.drawable.ic_assistant_black_24dp,
                    applicationContext.getString(R.string.notification_button_text),
                    contentPendingIntent
            )

    notify(NOTIFICATION_ID, builder.build())

}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
