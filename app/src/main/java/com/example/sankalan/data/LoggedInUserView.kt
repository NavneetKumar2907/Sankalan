package com.example.sankalan.data

data class LoggedInUserView(
    val name:String = "",
    val mobile:String = "",
    val uid:String = "",
    val email:String = "",
    val course:String = "",
    val institute:String = "",
    val year:Long = 0,
    val isVerified:Boolean =false
)
