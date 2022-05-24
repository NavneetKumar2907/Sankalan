package com.sankalan.ui.login.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AuthenticationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthenticationViewModel::class.java)) {
            return (AuthenticationViewModel(
                repository = AuthenticationRepository()
            )) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}