package com.example.photos.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.photos.R
import com.example.photos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolBar()
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.toolBar.apply {
            title = getString(R.string.app_name)
        })
    }

}