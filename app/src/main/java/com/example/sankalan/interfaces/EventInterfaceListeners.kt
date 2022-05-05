package com.example.sankalan.interfaces

import com.example.sankalan.data.DeleteResult
import com.example.sankalan.data.Events
import com.example.sankalan.data.Upload

interface EventInterfaceListeners {
    suspend fun delete(eventName:String): DeleteResult
    suspend fun edit(events:Events, eventName: String): Upload
    suspend fun deleteAll(): DeleteResult
}