package com.example.sankalan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sankalan.data.Events
import com.example.sankalan.data.LoggedInUserView
import com.example.sankalan.ui.login.data.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel():ViewModel() {
    private val user:FirebaseUser? = Firebase.auth.currentUser
    private val database = FirebaseDatabase.getInstance()

    val databaseUser = database.getReference("Users").child(user?.uid.toString())
    val postListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val post = snapshot.getValue<LoggedInUser>()
            val u = LoggedInUserView(
                name = post?.name.toString(),
                institute = post?.institute.toString(),
                course = post?.course.toString(),
                year =  post?.year.toString().toLong(),
                mobile = post?.mobile.toString(),
                isVerified = user?.isEmailVerified == true,
                email = user?.email.toString()
            )
            _userData.postValue(u)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error!", "Can't Retreive ${error.details}")
        }
    }

    private val _userData : MutableLiveData<LoggedInUserView> by lazy{
        MutableLiveData<LoggedInUserView>().also {
            loadUserDetails()
        }
    }
    val userData:LiveData<LoggedInUserView> = _userData


    private fun loadUserDetails(){
        databaseUser.addValueEventListener(postListener)
    }

    fun logout(){
        Firebase.auth.signOut()
    }


    private val eventList: MutableLiveData<ArrayList<Events>> by lazy {
        MutableLiveData<ArrayList<Events>>().also {
            loadEvent()
        }
    }


    fun getEvent(): MutableLiveData<ArrayList<Events>> {
        return eventList
    }

    private fun loadEvent() {
        GlobalScope.launch {

            val databaseEvent = database.getReference("Events")
            databaseEvent.get().addOnSuccessListener {
                if (it.exists()) {
                    val list = arrayListOf<Events>()
                    for (childEventname in it.children) {
                        val obj = Events(childEventname.key.toString())
                        for (child in childEventname.children) {
                            when (child.key.toString()) {
                                "Description" -> obj.eventDescription = child.value.toString()
                                "Image" ->  obj.eventPoster = child.value.toString()
                                "Type"->    obj.eventType = child.value.toString()
                                "Team" ->   obj.team = child.value as Boolean
                                "Venue" -> obj.eventVenue = child.value.toString()
                                "Time" -> obj.eventTiming = child.value.toString()
                                "Coordinator" -> obj.eventCoordinator = child.value.toString()
                                else -> Log.e(
                                    "Error in RECIEVING",
                                    "${child} :  DONT KNOW THE ERROR!!"
                                )
                            }
                        }

                        list.add(obj)

                    }

                    eventList.value = list
                } else {
                    Log.w("Error", "NOT found data.")
                }
            }
                .addOnFailureListener {
                    Log.w("Error", "DATABSE NOT FOUND.")
                }


        }
    }

}