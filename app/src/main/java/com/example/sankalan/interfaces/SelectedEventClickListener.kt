package com.example.sankalan.interfaces

import android.graphics.drawable.Drawable
import com.example.sankalan.data.TeamMembers

interface SelectedEventClickListener {
    fun selectedEvent(position:Int)
    fun Registration(team:TeamMembers = TeamMembers())
}