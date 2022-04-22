package com.example.sankalan.ui.login.model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.sankalan.R
import com.example.sankalan.ui.login.data.LoggedInUser
import com.example.sankalan.ui.login.data.LoginResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CompletableDeferred

class AuthenticationRepository {


    private val auth = FirebaseAuth.getInstance()
    val isLogin: Boolean
        get() = auth.currentUser != null


    suspend fun login(email: String, password: String):LoginResult {
        val def = CompletableDeferred<LoginResult>()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    //success in login.
                    def.complete(LoginResult(success = R.string.Success))
                } else {
                    //failed login.
                    Log.w("Login Failed inside Su", "${it.exception}")
                    def.complete(LoginResult(failed = it.exception!!.message))
                }
            }
            .addOnFailureListener {
                Log.w("Login Failed", "${it}")
                def.complete(LoginResult(failed = it.message))
            }
        return def.await()

    }

    suspend fun signUp(email: String, password: String, data: LoggedInUser):LoginResult {
        val def = CompletableDeferred<LoginResult>()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    //success in login.
                    uploadUserData(auth.currentUser?.uid, data)
                    Log.w("SignUp Sucess", "${it}")
                    def.complete(LoginResult(success = R.string.Success))


                } else {
                    //failed login.
                    Log.w("SignUp Failed", "${it.exception}")
                    def.complete(LoginResult(failed = it.exception!!.message))

                }
            }
            .addOnFailureListener {
                Log.w("SignUp Failed", "${it}")
                def.complete(LoginResult(failed = it.message))

            }
        return def.await()
    }

    private fun uploadUserData(uid: String?, data: LoggedInUser) {
        val database = FirebaseDatabase.getInstance().getReference("Users")
        if (uid != null) {
            database.child(uid).setValue(data)
                .addOnSuccessListener {
                    Log.w("User", "Data Saved")
                    auth.currentUser?.sendEmailVerification()
                }
                .addOnFailureListener {
                    Log.w("User Data ", "Not Saved ${it}")
                }
        } else {
            Log.w("Error", "Firebase UID Error!!.")
        }
    }

    fun logout() {
        if (isLogin){
            auth.signOut()
        }
    }

}