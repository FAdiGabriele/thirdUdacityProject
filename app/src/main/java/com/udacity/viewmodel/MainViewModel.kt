package com.udacity.viewmodel

import android.app.Application
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.udacity.R
import com.udacity.util.sendNotification


class MainViewModel(application: Application) : AndroidViewModel(application) {


    private var downloadID: Long = 0
    private val applicationContext = application
    val notificationManager: NotificationManager = ContextCompat.getSystemService(
        applicationContext,
        NotificationManager::class.java
    ) as NotificationManager


    //TODO: check a new way to get the connection
     fun isNetworkAvailable(): Boolean {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun download(url: String, title: String, notificationMessage: String) {
        val downloadManager = applicationContext.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        /*
        I haven't managed the case where the device is without internet connection
        because I see that the download start automatically when internet return
         */
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription(applicationContext.getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)


        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

        notificationManager.sendNotification(notificationMessage, applicationContext)

    }
}