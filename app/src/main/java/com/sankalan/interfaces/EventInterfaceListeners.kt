package com.sankalan.interfaces

import com.sankalan.data.DeleteResult
import com.sankalan.data.Events
import com.sankalan.data.Upload

interface EventInterfaceListeners {
    suspend fun delete(eventName: String): DeleteResult
    suspend fun edit(events: Events, eventName: String): Upload
    suspend fun deleteAll(): DeleteResult
}