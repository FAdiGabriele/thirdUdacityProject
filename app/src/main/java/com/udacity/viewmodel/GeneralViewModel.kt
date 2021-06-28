package com.udacity.viewmodel

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.udacity.util.DownloadHelper
import com.udacity.util.DownloadTracker
import com.udacity.util.sendNotification

class GeneralViewModel(application: Application) : AndroidViewModel(application) {

    private val applicationContext = application

    val downloadHelper = DownloadHelper(applicationContext)

    private var notificationMessage : String = ""
    private var fileName : String = ""

    val notificationManager: NotificationManager = ContextCompat.getSystemService(
        applicationContext,
        NotificationManager::class.java
    ) as NotificationManager

    private var _progressTracker = MutableLiveData(-1)
    val progressTracker : LiveData<Int>
        get() = _progressTracker

    init {
        setupLiveData()
    }

    private fun setupLiveData(){
        downloadHelper.downloadTracker.observeForever { downloadTrackerValue ->
            when(downloadTrackerValue!!){ // I'm sure that this value can't be null because the Enum class has a default value
                DownloadTracker.DOWNLOAD_NOT_STARTED -> {  //default value
                    Log.e("download","stato - no")
                    _progressTracker.value = -1
                }
                DownloadTracker.DOWNLOAD_IN_PROGRESS -> {
                    Log.e("download","stato - in corso")
                }
                DownloadTracker.DOWNLOAD_FAILED -> {
                    Log.e("download","stato - fallito")
                    //it send notification that contain the message given and the status of the download
                    notificationManager.sendNotification(fileName, notificationMessage, downloadTrackerValue , applicationContext)
                    _progressTracker.value = 100
                }

                DownloadTracker.DOWNLOAD_SUCCESSFUL -> {
                    Log.e("download","stato - finito")
                    //it send notification that contain the message given and the status of the download
                    notificationManager.sendNotification(fileName, notificationMessage, downloadTrackerValue , applicationContext)
                    _progressTracker.value = 100
                }
            }
        }

        downloadHelper.progressTracker.observeForever { progressTrackerValue ->
            _progressTracker.value = progressTrackerValue
        }
    }


    /*
    This is the simples way to get the internet connection, I use it only for display a toast
    But I know that the correct way is to do it in asynchronous way
    */
     fun isNetworkAvailable(): Boolean {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    fun download(url: String, title: String, notificationMessage: String) {

        downloadHelper.startDownload(url, title)
        this.fileName = title
        this.notificationMessage = notificationMessage
    }



}