package com.ducs.sankalan.ui.login.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ducs.sankalan.R
import com.ducs.sankalan.data.LoggedInUserView
import com.ducs.sankalan.ui.login.data.LoginFormState
import com.ducs.sankalan.ui.login.data.LoginResult
import kotlinx.coroutines.launch

class AuthenticationViewModel(private val repository: AuthenticationRepository) : ViewModel() {
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginForm: LiveData<LoginFormState> = _loginForm


    private val _signUpForm = MutableLiveData<LoginFormState>()



    private val passwordRegex =
        Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$")
    val result_login = MutableLiveData<LoginResult>()
    val result_signup = MutableLiveData<LoginResult>()

    fun login(email: String, password: String) {
        if (isValidEmail(email) && isValidPassword(password)) {
            viewModelScope.launch {
                val v: LoginResult = repository.login(email, password)
                result_login.value = v
            }

        } else {
            _loginForm.value = LoginFormState(
                emailError = R.string.invalid_email,
                passError = R.string.invalid_password
            )
        }

    }

    fun signUp(email: String, password: String, data: LoggedInUserView) {
        viewModelScope.launch {
            val res: LoginResult = repository.signUp(email, password, data)
            result_signup.value = res
        }

    }


    fun onLoginDataChange(email: String, password: String) {
        if (!isValidEmail(email)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_email)
        } else if (!isValidPassword(password)) {
            _loginForm.value = LoginFormState(passError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isValid = true)
        }
    }


    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return passwordRegex.matches(password) && password.length > 5
    }


}