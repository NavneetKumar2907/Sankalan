package com.example.sankalan.interfaces

import com.example.sankalan.data.Teams

interface TeamEditListener {
    fun openEdit(data: Teams, pos: Boolean = false)
}