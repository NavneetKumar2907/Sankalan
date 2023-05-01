package com.ducs.sankalan.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.ducs.sankalan.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SplashScreen : AppCompatActivity() {
    private val firebaseAuth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        setContentView(R.layout.activity_splash_screen)

        //Animators
        val animRotatorOne = AnimationUtils.loadAnimation(this,R.anim.rotator_one)
        val animRotatorTwo = AnimationUtils.loadAnimation(this,R.anim.rotator_two)
        val animRotatorThree = AnimationUtils.loadAnimation(this,R.anim.rotator_three)
        val animRotatorFour = AnimationUtils.loadAnimation(this,R.anim.rotator_four)

        //Setting Up Animation
        findViewById<ImageView>(R.id.rotator_one_iv).startAnimation(animRotatorOne)
        findViewById<ImageView>(R.id.rotator_two_iv).startAnimation(animRotatorTwo)
        findViewById<ImageView>(R.id.rotator_three_iv).startAnimation(animRotatorThree)
        findViewById<ImageView>(R.id.rotator_four_iv).startAnimation(animRotatorFour)

        //Handler
        val handler = Handler()
        handler.postDelayed(Runnable {
            //The following code will execute after the 5 seconds.
            try {
                // CHeck if already Login or Not
                check()
            } catch (ignored: Exception) {
                ignored.printStackTrace()
            }

        }, 3000)

    }

    //Login User Check
    private fun check() {
        val currentUser = firebaseAuth.currentUser
        Log.w("firebase", "$currentUser")
        if (currentUser != null) {
            val i:Intent = if(currentUser.email == "admin@sankalan.com"){
                Intent(this, AdminActivity::class.java)
            }else{
                Intent(this, MainActivity::class.java)
            }
            //Already Login Go to Home Screen
            i.putExtra("user", currentUser)
            startActivity(i)
            this.finish()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            this.finish()
        }
    }
}