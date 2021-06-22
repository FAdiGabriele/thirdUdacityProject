package com.udacity.viewmodel

import android.app.Application
import android.app.DownloadManager
import android.app.NotificationManager
import android.net.Uri
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application : Application) : AndroidViewModel(application) {

    lateinit var notificationManager: NotificationManager
    private var downloadID: Long = 0

    fun download(url: String, title : String, description : String, downloadManager: DownloadManager ) {
        /*
        I haven't managed the case where the device is without internet connection
        because I see that the download start automatically when internet return
         */
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription(description)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)


        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }


}