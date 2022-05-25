package com.main.sankalan.activities

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.main.sankalan.R
import com.main.sankalan.databinding.LoginActivityBinding
import com.main.sankalan.ui.login.model.AuthenticationViewModel
import com.main.sankalan.ui.login.model.AuthenticationViewModelFactory


class LoginActivity : AppCompatActivity(){


    lateinit var binding: LoginActivityBinding
    lateinit var viewModel:AuthenticationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //For Full Screen
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        //Intializing binding
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initializing ViewModel
        viewModel = ViewModelProvider(
            this,
            AuthenticationViewModelFactory()
        ).get(AuthenticationViewModel::class.java)

        //Animation
        val animDrawable = findViewById<ConstraintLayout>(R.id.rootLayout).background as AnimationDrawable?
        animDrawable?.setEnterFadeDuration(10)
        animDrawable?.setExitFadeDuration(3000)
        animDrawable?.start()

    }

}