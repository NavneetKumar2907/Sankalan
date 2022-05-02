package com.example.sankalan.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sankalan.databinding.ActivityAdminBinding


lateinit var binding:ActivityAdminBinding

class Admin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}