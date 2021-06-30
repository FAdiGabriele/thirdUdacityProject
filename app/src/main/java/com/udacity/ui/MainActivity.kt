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
    private var url = ""
    private var name = ""


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

        binding.contentLayout.customButton.setOnClickListener{
            manageDownload()
        }

        setupLiveData()
    }

    /**
     *  When the user select some radioButton I clean the ediText and
     *  when the user select the editText I clean the radio buttons
     *  so the application doesn't get a conflict
     */
    private fun clearEditText(){
        binding.contentLayout.customLink.setText("")
        binding.contentLayout.customLink.setOnClickListener {
            if(binding.contentLayout.radioGroup.checkedRadioButtonId != -1)
            binding.contentLayout.radioGroup.clearCheck()
        }
    }

    private fun setupLiveData() {
        /*
        With this LiveData we can observe the % of the download progress
         */
        viewModel.progressTracker.observe(this, Observer { progress ->
            when(progress){
                -1->{
                    //no Download is active
                    binding.contentLayout.radioGroup.clearCheck()
                    binding.contentLayout.customLink.setText("")
                }
                else -> {
                    //We set the % value at the bottom so we can show the progress to the User
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
                        resources.getString(R.string.wait_for_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }


                var insertedUrl = binding.contentLayout.customLink.text.toString()

                if(isURLWellWritten(insertedUrl)){

                    if(insertedUrl.startsWith("www.")){
                        insertedUrl = "https://$insertedUrl"
                    }

                    viewModel.download(
                            insertedUrl,
                            getString(R.string.custom_download),
                            "something"
                    )

                }else{
                    Toast.makeText(
                            this,
                            resources.getString(R.string.url_bad_written),
                            Toast.LENGTH_SHORT
                    ).show()
                }

            }

            else ->{
                Toast.makeText(
                    this,
                    resources.getString(R.string.error_toast_message),
                    Toast.LENGTH_SHORT
                ).show()
                //We should not wait if download did not start
                binding.contentLayout.customButton.setProgressValue(100)
            }
        }
    }


    private fun isURLWellWritten(url: String): Boolean {

        return url.startsWith("https://") || url.startsWith("http://") || url.startsWith("www.")

    }

}
