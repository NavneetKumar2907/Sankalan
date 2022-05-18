package com.example.sankalan.data

import android.graphics.Bitmap

data class Teams(val name:String = "",val phone:String="", val position:String="", val github:String = "", var image:String="", val instagram:String="", val linkedin:String = ""){
    var imageBitmap:Bitmap?=null
}
