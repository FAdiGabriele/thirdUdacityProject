package com.udacity.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.udacity.R
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.util.Constants
import com.udacity.util.cancelNotifications
import com.udacity.viewmodel.GeneralViewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    var timePassed = 0L
    private val viewModel: GeneralViewModel by lazy {
        ViewModelProvider(this).get(GeneralViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


        viewModel.notificationManager.cancelNotifications()

        binding.contentLayout.fileNameValue.text = intent.getStringExtra(Constants.FILE_NAME)
        binding.contentLayout.statusValue.text = intent.getStringExtra(Constants.STATUS)

        if(binding.contentLayout.statusValue.text == resources.getString(R.string.status_failed) ||
                binding.contentLayout.statusValue.text == resources.getString(R.string.status_error)){
            binding.contentLayout.statusValue.setTextColor(Color.RED)
        }

        loadProgressValue()


        binding.contentLayout.button.setOnClickListener {
            returnToMainScreen()
        }


    }


    private fun returnToMainScreen(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    private fun loadProgressValue(){
        val partOfSecond = 400L
        val fullTime = 6000L

       val countDownTimer = object : CountDownTimer(fullTime,partOfSecond){
           override fun onTick(p0: Long) {
               timePassed += partOfSecond
               binding.contentLayout.yellowCircle.percentValue = (timePassed* 360 /fullTime).toFloat()
           }

           override fun onFinish() {
              returnToMainScreen()
           }
       }

        countDownTimer.start()

    }

}
