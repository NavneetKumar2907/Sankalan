package com.sankalan.interfaces

import com.sankalan.data.Teams

interface TeamEditListener {
    fun openEdit(data: Teams, pos: Boolean = false)
}