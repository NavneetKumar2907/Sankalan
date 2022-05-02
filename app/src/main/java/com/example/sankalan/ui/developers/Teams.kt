package com.example.sankalan.ui.developers

import android.graphics.Bitmap

data class Teams(val name:String = "", val position:String="", val github:String = "", val image:String="", val instagram:String="", val linkedin:String = ""){
    var imageBitmap:Bitmap?=null
}
