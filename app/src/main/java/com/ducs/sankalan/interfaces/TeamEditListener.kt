package com.ducs.sankalan.interfaces

import com.ducs.sankalan.data.Teams

interface TeamEditListener {
    fun openEdit(data: Teams, pos: Boolean = false)
}