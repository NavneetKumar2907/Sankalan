package com.main.sankalan.interfaces

import com.main.sankalan.data.DeleteResult
import com.main.sankalan.data.Events
import com.main.sankalan.data.Upload

interface EventInterfaceListeners {
    suspend fun delete(eventName: String): DeleteResult
    suspend fun edit(events: Events, eventName: String): Upload
    suspend fun deleteAll(): DeleteResult
}