package com.udacity.ui

import android.app.DownloadManager
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.udacity.R
import com.udacity.databinding.ActivityMainBinding
import com.udacity.util.Constants.download_link_glide
import com.udacity.util.Constants.download_link_loadapp
import com.udacity.util.Constants.download_link_retrofit
import com.udacity.util.Constants.download_name_glide
import com.udacity.util.Constants.download_name_loadapp
import com.udacity.util.Constants.download_name_retrofit
import com.udacity.util.Constants.selected_glide
import com.udacity.util.Constants.selected_loadapp
import com.udacity.util.Constants.selected_retrofit
import com.udacity.viewmodel.GeneralViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: GeneralViewModel by lazy {
        ViewModelProvider(this).get(GeneralViewModel::class.java)
    }

    private lateinit var binding : ActivityMainBinding
//    private lateinit var pendingIntent: PendingIntent
//    private lateinit var action: NotificationCompat.Action
    private var url = ""
    private var name = ""

    private val buttonCLickListener = View.OnClickListener {
        manageDownload()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


        binding.contentLayout.radioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId) {
                group[selected_glide].id -> {
                    url = download_link_glide
                    name = download_name_glide
                    clearEditText()
                }
                group[selected_loadapp].id -> {
                    url = download_link_loadapp
                    name = download_name_loadapp
                    clearEditText()
                }
                group[selected_retrofit].id -> {
                    url = download_link_retrofit
                    name = download_name_retrofit
                    clearEditText()
                }
            }
        }

        registerReceiver(viewModel.downloadHelper.broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.contentLayout.customButton.setOnClickListener(buttonCLickListener)

        setupLiveData()
    }

    /*
         When the user select some radioButton it disable the ediText that
         allow to write a custom link and I clean it for avoid confusing
          */
    fun clearEditText(){
        binding.contentLayout.customLink.setText("")
        binding.contentLayout.customLink.isEnabled = false
    }

    private fun setupLiveData() {
        viewModel.progressTracker.observe(this, Observer { progress ->
            when(progress){
                -1->{
                    Log.e("Progress","download finito, codice $progress")
                    if(!binding.contentLayout.customLink.isEnabled) {
                        binding.contentLayout.customLink.isEnabled = true
                        binding.contentLayout.customLink.isFocusable = true
                    }
                    binding.contentLayout.radioGroup.clearCheck()
                }
                else -> {
                   Log.e("Progress","download al $progress %")
                    binding.contentLayout.customButton.setProgressValue(progress)
                }
            }

        })
    }

    //todo: replace something
    //todo: string values
    private fun manageDownload(){

        when{
            /*
            If uri is populated i know that a radio button is clicked
            */
            url.isNotBlank() -> {
                if(!viewModel.isNetworkAvailable()){
                    Toast.makeText(
                        this,
                        "We are waiting for internet connection and we will start your download",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                viewModel.download(url, name, "something")

            }

            /*
            If customLink is populated i know that radioButton is not clicked
            and the user wants a custom link
            */
            binding.contentLayout.customLink.text.toString().isNotBlank() -> {
                if(!viewModel.isNetworkAvailable()){
                    Toast.makeText(
                        this,
                        "We are waiting for internet connection and we will start your download",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                viewModel.download(
                    binding.contentLayout.customLink.text.toString(),
                    getString(R.string.custom_download),
                    "something"
                )

            }

            else ->{
                Toast.makeText(
                    this,
                    resources.getString(R.string.error_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
                binding.contentLayout.customButton.setProgressValue(100)
            }
        }
    }

}
