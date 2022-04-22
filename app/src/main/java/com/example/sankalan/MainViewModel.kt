package com.example.sankalan

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sankalan.data.*
import com.example.sankalan.ui.login.data.LoggedInUser
import com.example.sankalan.ui.login.data.LoginResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.operation.ListenComplete
import com.google.firebase.database.core.operation.OperationSource
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel():ViewModel() {

    private val user:FirebaseUser? = Firebase.auth.currentUser
    private val database = FirebaseDatabase.getInstance()


    private val databaseUser = database.getReference("Users").child(user?.uid.toString())
    private val userListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val post = snapshot.getValue<LoggedInUser>()
            Log.w("Post Value","${post}")
         try {
             val u = LoggedInUserView(
                 name = post?.name.toString(),
                 institute = post?.institute.toString(),
                 course = post?.course.toString(),
                 year =  post?.year.toString().toInt(),
                 mobile = post?.mobile.toString(),
                 isVerified = user?.isEmailVerified == true,
                 email = user?.email.toString()
             )
             _userData.postValue(u)
         }catch (e:Exception){
             Log.w("Error Loading details",e.message.toString())
         }
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
        databaseUser.addValueEventListener(userListener)
    }

    fun logout(){
        Firebase.auth.signOut()
    }

//EVENT LIST LIVE VARIABLE
    private val eventList: MutableLiveData<ArrayList<Events>> by lazy {
        MutableLiveData<ArrayList<Events>>().also {
            loadEvent()
        }
    }
//Getter of Eventlist
    fun getEvent(): MutableLiveData<ArrayList<Events>> {
        return eventList
    }
    //Database refrence for eventlist
    private val databaseEvent = database.getReference("Events")

//event list value listener
    private val eventListener = object:ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val list = arrayListOf<Events>()
                for (childEventname in snapshot.children) {
                    list.add(childEventname.getValue<Events>()!!)
                }
                eventList.value = list
            } else {
                Log.w("Error", "NOT found data.")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error", error.details)
        }

    }
//database attach to listener
    private fun loadEvent() {
        databaseEvent.addValueEventListener(eventListener)
    }
//Edit User details

    fun editUserDetail(newDetail:LoggedInUser){
        try {
            databaseUser.setValue(newDetail)
        }catch (e:Exception){
            Log.w("Error!","Error in uploading Details.")
        }
    }

    val databaseRegisterEvent = database.getReference("RegisteredEvents").child(user?.uid.toString())

    private val registeredEventValueListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            val eventMember = arrayListOf<RegisteredEvents>()
            for(eventName in snapshot.children){
                Log.w("W","${eventName.key},${eventName.value}")
                val res = eventName.getValue<TeamMembers>()
                eventMember.add(RegisteredEvents(eventName = eventName.key.toString(), members = res!!))
            }
            _eventWiseMember.value = eventMember
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("ERROR",error.message)
        }

    }

    private val _eventWiseMember:MutableLiveData<ArrayList<RegisteredEvents>> by lazy {
        MutableLiveData<ArrayList<RegisteredEvents>>().also {
            loadRegisteredEvents()
        }
    }


        val eventWiseMember:LiveData<ArrayList<RegisteredEvents>> = _eventWiseMember


        private fun loadRegisteredEvents(){
            databaseRegisterEvent.addValueEventListener(registeredEventValueListener)
        }


        suspend fun registerForEvent(eventName:String, team:Boolean=false, members:TeamMembers = TeamMembers()):RegistrationSuccess{
            val def = CompletableDeferred<RegistrationSuccess>()

            databaseRegisterEvent.get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        //Already Registered for some events
                        if(it.hasChild(eventName)){
                            def.complete(RegistrationSuccess(failed = "Already Registered"))
                            Log.w("Success","Registration Already exist.")
                        }else{
                            // Not registered for this event
                            //Register
                            if (team) {
                                databaseRegisterEvent.child(eventName).setValue(members)
                                def.complete(RegistrationSuccess(succes =R.string.sucess_register))

                            } else {
                                databaseRegisterEvent.child(eventName).setValue(TeamMembers())
                                def.complete(RegistrationSuccess(succes =R.string.sucess_register))
                                Log.w("Failed else","Registration Error individual.")

                            }
                        }

                    } else {
                        //Register
                        if (team) {
                            databaseRegisterEvent.child(eventName).setValue(members)
                            def.complete(RegistrationSuccess(succes =R.string.sucess_register))

                        } else {
                            databaseRegisterEvent.child(eventName).setValue(TeamMembers())
                            def.complete(RegistrationSuccess(succes =R.string.sucess_register))
                            Log.w("Failed else","Registration Error individual.")

                        }

                    }
                }
                .addOnFailureListener {
                    def.complete(RegistrationSuccess(failed = it.message.toString()))

                    Log.w("Error Register", it.message.toString())
                }
            return def.await()
        }
    }
