package com.udacity.ui

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.udacity.viewmodel.MainViewModel
import com.udacity.R
import com.udacity.util.Constants.download_link_glide
import com.udacity.util.Constants.download_link_loadapp
import com.udacity.util.Constants.download_link_retrofit
import com.udacity.util.Constants.download_name_glide
import com.udacity.util.Constants.download_name_loadapp
import com.udacity.util.Constants.download_name_retrofit
import com.udacity.util.Constants.selected_glide
import com.udacity.util.Constants.selected_loadapp
import com.udacity.util.Constants.selected_retrofit
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val buttonCLickListener = View.OnClickListener {
        //TODO: manage animation when there isn't internet
        //TODO: manage animation
        manageDownload()
    }

    private lateinit var binding : ActivityMainBinding
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var url = ""
    private var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        //TODO: delete nani
        binding.contentLayout.nani.setOnClickListener(buttonCLickListener)

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

        binding.contentLayout.customButton.setOnClickListener(buttonCLickListener, null, viewModel.isNetworkAvailable())
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun manageDownload(){

        when{
            /*
            If uri is populated i know that a radio button is clicked
            */
            url.isNotBlank() -> {
                viewModel.download(url, name, "something")
            }

            /*
            If customLink is populated i know that radioButton is not clicked
            and the user wants a custom link
            */
            binding.contentLayout.customLink.text.toString().isNotBlank() -> {
                viewModel.download(binding.contentLayout.customLink.text.toString(), getString(R.string.custom_download), "something")
            }

            else ->{
                Toast.makeText(this, resources.getString(R.string.error_toast_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

}
