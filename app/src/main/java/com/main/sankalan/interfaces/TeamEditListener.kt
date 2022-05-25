package com.main.sankalan.interfaces

import com.main.sankalan.data.Teams

interface TeamEditListener {
    fun openEdit(data: Teams, pos: Boolean = false)
}