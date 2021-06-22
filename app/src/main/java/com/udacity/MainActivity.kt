package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.Constants.download_link_glide
import com.udacity.Constants.download_link_loadapp
import com.udacity.Constants.download_link_retrofit
import com.udacity.Constants.download_name_glide
import com.udacity.Constants.download_name_loadapp
import com.udacity.Constants.download_name_retrofit
import com.udacity.Constants.selected_glide
import com.udacity.Constants.selected_loadapp
import com.udacity.Constants.selected_retrofit
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var binding : ActivityMainBinding
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var url = ""
    private var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        binding.contentLayout.nani.setOnClickListener {
           manageDownload()
        }

        binding.contentLayout.radioGroup.setOnCheckedChangeListener { group, checkedId ->

            /*
            When the user select some radioButton it disable the ediText that
            allow to write a custom link and I clean it for avoid confusing
             */
            binding.contentLayout.customLink.setText("")
            binding.contentLayout.customLink.isEnabled = false
            when(checkedId) {
                selected_glide -> {
                    url = download_link_glide
                    name = download_name_glide
                }
                selected_loadapp -> {
                    url = download_link_loadapp
                    name = download_name_loadapp
                }
                selected_retrofit -> {
                    url = download_link_retrofit
                    name = download_name_retrofit
                }
            }
        }

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.contentLayout.customButton.setOnClickListener {
            //TODO: manage animation when there isn't internet
            //TODO: manage animation
            //TODO: bring here the code of nani button and delete it
          //  download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download(url: String, title : String) {
        //TODO: notification  message
        notificationManager.sendNotification(
            "notification_message",
            this
        )
        /*
        I haven't managed the case where the device is without internet connection
        because I see that the download start automatically when internet return
         */
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(title)
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }

    private fun manageDownload(){
        when{
            /*
            If uri is populated i know that a radio button is clicked
            */
            url.isNotBlank() -> {
                download(url, name)
            }

            /*
            If customLink is populated i know that radioButton is not clicked
            and the user wants a custom link
            */
            binding.contentLayout.customLink.text.toString().isNotBlank() -> {
                download(binding.contentLayout.customLink.text.toString(), getString(R.string.custom_download))
            }

            else ->{
                Toast.makeText(this, resources.getString(R.string.error_toast_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

}
