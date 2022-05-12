package com.example.sankalan.data

data class LoggedInUserView(
    val name:String = "",
    val mobile:String = "",
    var email:String = "",
    val course:String = "",
    val institute:String = "",
    val year:Int = 0,
    var isVerified:Boolean =false
){
    var uid:String = ""
}
