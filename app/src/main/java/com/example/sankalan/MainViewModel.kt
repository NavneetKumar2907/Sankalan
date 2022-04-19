package com.example.sankalan

import android.util.Log
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
         try {
             val u = LoggedInUserView(
                 name = post?.name.toString(),
                 institute = post?.institute.toString(),
                 course = post?.course.toString(),
                 year =  post?.year.toString().toInt(),
                 mobile = post?.mobile.toString(),
                 isVerified = post!!.isVerified,
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
    private val _registeredEvents:MutableLiveData<ArrayList<String>> by lazy {
        MutableLiveData<ArrayList<String>>().also {
            loadRegisteredEvents()
        }
    }

    val registeredEvents:LiveData<ArrayList<String>> = _registeredEvents

    private val _eventWiseMember:MutableLiveData<ArrayList<RegisteredEvents>> by lazy {
        MutableLiveData<ArrayList<RegisteredEvents>>()
    }

    val eventWiseMember:LiveData<ArrayList<RegisteredEvents>> = _eventWiseMember

    val registeredEventValueListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.exists()){
                val event = arrayListOf<String>()
                val eventMember = arrayListOf<RegisteredEvents>()
                for (events in snapshot.children){
                   val members = events.getValue<TeamMembers>()
                   event.add(events.key.toString())
                    eventMember.add(RegisteredEvents(eventName = events.key.toString(), members = members!!))
                }
                _registeredEvents.postValue(event)
                _eventWiseMember.postValue(eventMember)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }

    fun loadRegisteredEvents(){
        try{
            databaseRegisterEvent.addValueEventListener(registeredEventValueListener)
        }catch (e:Exception){
            Log.w("Error","Not registered in any event.")
        }
    }

    val registraionresult = MutableLiveData<RegistrationSuccess>()

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
                                     databaseRegisterEvent.child(eventName).setValue("sucess")
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
                             databaseRegisterEvent.child(eventName).setValue("sucess")
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