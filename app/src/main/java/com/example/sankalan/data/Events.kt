package com.example.sankalan.data

import android.graphics.Bitmap
import android.graphics.drawable.Drawable


data class Events (
    val EventName: String = "",
    var Description:String = "NONE",
    var Image: String = "NONE", //url
    var Type: String = "NONE" ,// Technical nontechnical
    var Team: Boolean = false,
    var Venue: String = "NONE",
    var Time: String = "00:00",
    var Coordinator:String = "XXXX"
){
    var image_drawable:Bitmap?=null
}