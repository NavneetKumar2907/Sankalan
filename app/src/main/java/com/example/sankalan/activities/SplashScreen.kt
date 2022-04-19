package com.example.sankalan.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sankalan.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreen : AppCompatActivity() {
    private val firebaseAuth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val handler = Handler()
        handler.postDelayed(Runnable {
            //The following code will execute after the 5 seconds.
            try {
                // CHeck if already Login or Not
                check()
            } catch (ignored: Exception) {
                ignored.printStackTrace()
            }

        }, 5000)

    }

    private fun check() {
        val currentUser = firebaseAuth.currentUser
        Log.w("firebase", "${currentUser}")
        if (currentUser != null) {
            //Already Login Go to Home Screen
            val i = Intent(this, MainActivity::class.java)
            i.putExtra("user", currentUser)
            startActivity(i)
            this.finish()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            this.finish()
        }
    }
}