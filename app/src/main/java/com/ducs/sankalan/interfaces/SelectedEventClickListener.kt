package com.ducs.sankalan.interfaces

import com.ducs.sankalan.data.RegistrationSuccess
import com.ducs.sankalan.data.TeamMembers

interface SelectedEventClickListener {
    fun selectedEvent(position: Int)
    suspend fun Registration(
        team: TeamMembers = TeamMembers(),
        teamName: String = ""
    ): RegistrationSuccess
}