package com.example.sankalan.data

data class RegisteredEvents(val eventName:String, val members: TeamMembers = TeamMembers(), val individual:String = "",val teamName:String = "")
