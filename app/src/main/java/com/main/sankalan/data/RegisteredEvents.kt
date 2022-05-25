package com.main.sankalan.data

/**
 * Data Class For Registered Events Values.
 */
data class RegisteredEvents(
    val eventName: String,
    val members: TeamMembers = TeamMembers(),
    val individual: String = "",
    val teamName: String = ""
)
