package com.example.sankalan.data


data class Events (
    val EventName: String = "",
    var Description:String = "NONE",
    var Image: String = "NONE", //url
    var Type: String = "NONE" ,// Technical nontechnical
    var Team: Boolean = false,
    var Venue: String = "NONE",
    var Time: String = "00:00",
    var Coordinator:String = "XXXX"
)