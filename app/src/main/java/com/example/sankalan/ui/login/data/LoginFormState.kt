package com.example.sankalan.ui.login.data

data class LoginFormState(
    val emailError: Int? = null,
    val passError: Int? = null,
    val isValid: Boolean = false
)
