package com.sankalan.interfaces

import com.sankalan.data.RegistrationSuccess
import com.sankalan.data.TeamMembers

interface SelectedEventClickListener {
    fun selectedEvent(position: Int)
    suspend fun Registration(
        team: TeamMembers = TeamMembers(),
        teamName: String = ""
    ): RegistrationSuccess
}