package com.example.sankalan.interfaces

import android.graphics.drawable.Drawable
import com.example.sankalan.data.RegistrationSuccess
import com.example.sankalan.data.TeamMembers

interface SelectedEventClickListener {
    fun selectedEvent(position:Int)
    suspend fun Registration(team:TeamMembers = TeamMembers(), teamName:String=""):RegistrationSuccess
}