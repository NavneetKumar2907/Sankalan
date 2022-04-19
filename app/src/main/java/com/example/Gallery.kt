package com.example.sankalan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Gallery(sankalan1: Int) : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_gallery)

    }

    fun gallery(): List<Gallery> {
        return listOf<Gallery>(
            Gallery(R.drawable.sankalan1),
            Gallery(R.drawable.sankalan2),
            Gallery(R.drawable.sankalan3),
            Gallery(R.drawable.sankalan4),
            Gallery(R.drawable.sankalan5),
            Gallery(R.drawable.sankalan6),
            Gallery(R.drawable.sankalan7),
            Gallery(R.drawable.sankalan8),
            Gallery(R.drawable.sankalan9)
        )
    }
}


