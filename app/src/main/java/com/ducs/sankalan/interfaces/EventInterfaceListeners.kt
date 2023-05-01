package com.ducs.sankalan.interfaces

import com.ducs.sankalan.data.DeleteResult
import com.ducs.sankalan.data.Events
import com.ducs.sankalan.data.Upload

interface EventInterfaceListeners {
    suspend fun delete(eventName: String): DeleteResult
    suspend fun edit(events: Events, eventName: String): Upload
    suspend fun deleteAll(): DeleteResult
}