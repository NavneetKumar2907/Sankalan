package com.main.sankalan.interfaces

import com.main.sankalan.data.RegistrationSuccess
import com.main.sankalan.data.TeamMembers

interface SelectedEventClickListener {
    fun selectedEvent(position: Int)
    suspend fun Registration(
        team: TeamMembers = TeamMembers(),
        teamName: String = ""
    ): RegistrationSuccess
}