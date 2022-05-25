package com.main.sankalan.ui.login.model

import android.util.Log
import com.main.sankalan.R
import com.main.sankalan.data.LoggedInUserView
import com.main.sankalan.ui.login.data.LoginResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CompletableDeferred

class AuthenticationRepository {


    private val auth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): LoginResult {
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

    suspend fun signUp(email: String, password: String, data: LoggedInUserView): LoginResult {
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

    private fun uploadUserData(uid: String?, data: LoggedInUserView) {
        val database = FirebaseDatabase.getInstance().getReference("Users")
        data.uid = FirebaseAuth.getInstance().uid.toString()
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


}