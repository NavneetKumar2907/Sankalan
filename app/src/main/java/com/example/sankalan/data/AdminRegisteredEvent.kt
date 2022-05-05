package com.example.sankalan.data

data class AdminRegisteredEvent(
    val eventName: String = "",
    val members:TeamMembers = TeamMembers()
)