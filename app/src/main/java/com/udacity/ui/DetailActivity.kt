package com.udacity.ui

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.udacity.util.cancelNotifications
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.viewmodel.GeneralViewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: GeneralViewModel by lazy {
        ViewModelProvider(this).get(GeneralViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        viewModel.notificationManager.cancelNotifications()
    }

}
