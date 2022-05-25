package com.main.sankalan.data

import android.graphics.Bitmap

/**
 * Data Class FOr Events Values.
 */
data class Events(
    val eventName: String = "",
    var Description: String = "NONE",
    var Image: String = "NONE", //url
    var Type: String = "NONE",// Technical nontechnical
    var Team: String = "",
    var Venue: String = "NONE",
    var timeHour: Int = 0,
    var timeMinute: Int = 0,
    var Coordinator: String = "XXXX",
    var rules: String = "----------------------------------",
    var teamSize:Int = 0
) {
    var image_drawable: Bitmap? = null
}