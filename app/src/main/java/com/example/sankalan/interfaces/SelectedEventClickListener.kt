package com.example.sankalan.interfaces

import android.graphics.drawable.Drawable

interface SelectedEventClickListener {
    fun selectedEvent(position:Int, poster:Drawable)
}