package com.udacity.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.R

class DownloadHelper(val context: Context) {

    var downloadId = -1L
    private var _downloadTracker = MutableLiveData<DownloadTracker>(DownloadTracker.DOWNLOAD_NOT_STARTED)
    val downloadTracker : LiveData<DownloadTracker>
        get() = _downloadTracker

    private var _progressTracker = MutableLiveData(-1)
    val progressTracker : LiveData<Int>
        get() = _progressTracker

    private val downloadManager: DownloadManager = context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager

    val broadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {

            val action = intent?.action
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                val downloadId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, -1
                )
                val query = DownloadManager.Query()
                query.setFilterById(downloadId)
                val c: Cursor = downloadManager.query(query)
                if (c.moveToFirst()) {
                    val columnIndex = c
                        .getColumnIndex(DownloadManager.COLUMN_STATUS)
                    _downloadTracker.value =
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            DownloadTracker.DOWNLOAD_SUCCESSFUL
                            //Download status is successful
                        }else{
                            DownloadTracker.DOWNLOAD_FAILED
                            //Download status is failed
                        }
                    resetDownloadTracker()
                }
            }

        }
    }

    fun startDownload(url: String, title: String){
        /*
        I haven't managed the case where the device is without internet connection
        because I see that the download start automatically when internet return
         */
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription(context.getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)


        downloadManager.enqueue(request)

        _downloadTracker.value = DownloadTracker.DOWNLOAD_IN_PROGRESS

        //It starts the download observer
        val myDownloads = Uri.parse(Constants.uri_my_download)
        context.contentResolver.registerContentObserver(myDownloads, true, DownloadObserver(Handler()))
    }

    private fun resetDownloadTracker(){
        _downloadTracker.value = DownloadTracker.DOWNLOAD_NOT_STARTED
    }

    inner class DownloadObserver(handler: Handler?) : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {

            try{
               downloadId = uri.toString().substring(uri.toString().lastIndexOf("/")+1).toLong()
            }catch (e: NumberFormatException){

                /*
                When we do another download, the uri resets and it isn't a number and get an exception.
                When i get this exception, I know that i still have not a pending download and I can reset the downloadId
                */
                downloadId = -1L

                return
            }

            Log.e("downloadId", "è $downloadId")

            getDownloadProgress(downloadId)
        }


        private fun getDownloadProgress(downloadId : Long){
            val query = DownloadManager.Query()
            query.setFilterById(downloadId)

            val c: Cursor = downloadManager.query(query)
            if (c.moveToNext()) {
                val sizeIndex: Int = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                val downloadedIndex: Int =
                    c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                val size: Long = c.getInt(sizeIndex).toLong()
                val downloaded: Long = c.getInt(downloadedIndex).toLong()

                if (size != -1L || size != 0L){

                    val downloadedSoFar = (downloaded * 100.0 / size).toInt()

                    _progressTracker.value =
                        if(downloadedSoFar < 0){
                            /*
                            If the user downloads a tiny file the progress sign values like -6690000 because the download is too fast for tracker
                            My goal here is normalize this value
                             */
                            val downloadedSoFarString = (downloadedSoFar * -1).toString().substring(0,2)
                            downloadedSoFarString.toInt()
                        }else{
                            downloadedSoFar
                        }
                }

            }
            c.close()
        }
    }
}