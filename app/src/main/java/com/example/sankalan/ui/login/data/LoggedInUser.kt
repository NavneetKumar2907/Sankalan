package com.example.sankalan.ui.login.data

data class LoggedInUser(
    val name:String = "",
    val mobile:String = "",
    val course:String = "",
    val institute:String = "",
    val year:Int = 0,
    val isVerified:Boolean = false
)
