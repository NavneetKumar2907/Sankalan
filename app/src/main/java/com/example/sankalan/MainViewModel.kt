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
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {

    private val user: FirebaseUser? = Firebase.auth.currentUser // Current User
    private val database = FirebaseDatabase.getInstance() // Database Instance
//========================================Database References=========================================================================================================================================================================================

    // Database References
    private val databaseUser = database.getReference("Users").child(user?.uid.toString()) // User Reference
    private val image_ref = Firebase.storage.reference.child("gallery") // Gallery Reference
    private val databaseEvent = database.getReference("Events") // Event Listener Reference
    private val databaseRegisterEvent = database.getReference("RegisteredEvents").child(user?.uid.toString()) // Registered Event Reference

//=========================================Listeners========================================================================================================================================================================================

    private val userListener = object : ValueEventListener {
        /**
         * User Data Change Listener.
         */
        override fun onDataChange(snapshot: DataSnapshot) {
            val post = snapshot.getValue<LoggedInUser>()
            Log.w("Post Value", "${post}")
            try {
                val u = LoggedInUserView(
                    name = post?.name.toString(),
                    institute = post?.institute.toString(),
                    course = post?.course.toString(),
                    year = post?.year.toString().toInt(),
                    mobile = post?.mobile.toString(),
                    isVerified = user?.isEmailVerified == true,
                    email = user?.email.toString()
                )
                _userData.postValue(u)
            } catch (e: Exception) {
                Log.w("Error Loading details", e.message.toString())
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error!", "Can't Retreive ${error.details}")
        }
    }

    //event list value listener
    private val eventListener = object : ValueEventListener {
        /**
         * Event list change listener.
         */
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

    private val registeredEventValueListener = object : ValueEventListener {
        /**
         * Registered Event Data change Listener.
         */
        override fun onDataChange(snapshot: DataSnapshot) {
            val eventMember = arrayListOf<RegisteredEvents>()
            for (eventName in snapshot.children) {
                Log.w("W", "${eventName.key},${eventName.value}")
                val res = eventName.getValue<TeamMembers>()
                eventMember.add(
                    RegisteredEvents(
                        eventName = eventName.key.toString(),
                        members = res!!
                    )
                )
            }
            _eventWiseMember.value = eventMember
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("ERROR", error.message)
        }

    }
//================================Live Data====================================================================================================================================================

    // User Live Data
    private val _userData: MutableLiveData<LoggedInUserView> by lazy {
        MutableLiveData<LoggedInUserView>().also {
            loadUserDetails()
        }
    }

    val userData: LiveData<LoggedInUserView> = _userData // getter of user data

    // Event Live Data
    private val eventList: MutableLiveData<ArrayList<Events>> by lazy {
        MutableLiveData<ArrayList<Events>>().also {
            loadEvent()
        }
    }

    // Getter of Event List
    fun getEvent(): MutableLiveData<ArrayList<Events>> {
        return eventList
    }

    // Gallery Live Data
    private val _images_gallery: MutableLiveData<ArrayList<String>> by lazy {
        MutableLiveData<ArrayList<String>>().also {
            loadImages()
        }
    }
    val images_gallery: LiveData<ArrayList<String>> = _images_gallery // Getter of gallery image lists

    //Registered Event Live Data
    private val _eventWiseMember: MutableLiveData<ArrayList<RegisteredEvents>> by lazy {
        MutableLiveData<ArrayList<RegisteredEvents>>().also {
            loadRegisteredEvents()
        }
    }


    val eventWiseMember: LiveData<ArrayList<RegisteredEvents>> = _eventWiseMember // Getter of Registered Events

//=====================================Loaders============================================================================================================================================================================================


    private fun loadUserDetails() {
        /**
         * Load User Details.
         */
        databaseUser.addValueEventListener(userListener)
    }

    private fun loadEvent() {
        /**
         * Load Events.
         */
        databaseEvent.addValueEventListener(eventListener)
    }

    fun loadImages() {
        /**
         *Load Gallery Images.
         */
        val temp = arrayListOf<String>()
        image_ref.listAll()
            .addOnSuccessListener {
                it.items.forEach { sto ->
                    sto.downloadUrl.addOnSuccessListener { u ->
                        temp.add(u.toString())
                    }
                }
                _images_gallery.postValue(temp)
            }
            .addOnFailureListener {
                Log.w("Faield List", it.message.toString())
            }
    }

    private fun loadRegisteredEvents() {
        databaseRegisterEvent.addValueEventListener(registeredEventValueListener)
    }

//====================================Functionality Functions=============================================================================================================================================================================================

    fun editUserDetail(newDetail: LoggedInUser) {
        /**
         * Edit user Details
         */
        try {
            databaseUser.setValue(newDetail)
        } catch (e: Exception) {
            Log.w("Error!", "Error in uploading Details.")
        }
    }

    suspend fun registerForEvent(
        eventName: String,
        team: Boolean = false,
        members: TeamMembers = TeamMembers()
    ): RegistrationSuccess {
        /**
         * Register for Event.
         */
        val def = CompletableDeferred<RegistrationSuccess>()

        databaseRegisterEvent.get()
            .addOnSuccessListener {
                if (it.exists()) {
                    //Already Registered for some events
                    if (it.hasChild(eventName)) {
                        def.complete(RegistrationSuccess(failed = "Already Registered"))
                        Log.w("Success", "Registration Already exist.")
                    } else {
                        // Not registered for this event
                        //Register
                        if (team) {

                            val memb1 = Firebase.auth.fetchSignInMethodsForEmail(members.member1)
                            val memb2 = Firebase.auth.fetchSignInMethodsForEmail(members.member2)
                            val memb3 = Firebase.auth.fetchSignInMethodsForEmail(members.member3)
                            val memb4 = Firebase.auth.fetchSignInMethodsForEmail(members.member4)

                            databaseRegisterEvent.child(eventName).setValue(members)

                            def.complete(RegistrationSuccess(succes = R.string.sucess_register))

                        } else {
                            databaseRegisterEvent.child(eventName).setValue(TeamMembers())
                            def.complete(RegistrationSuccess(succes = R.string.sucess_register))
                            Log.w("Failed else", "Registration Error individual.")

                        }
                    }

                } else {
                    //Register
                    if (team) {
                        databaseRegisterEvent.child(eventName).setValue(members)
                        def.complete(RegistrationSuccess(succes = R.string.sucess_register))

                    } else {
                        databaseRegisterEvent.child(eventName).setValue(TeamMembers())
                        def.complete(RegistrationSuccess(succes = R.string.sucess_register))
                        Log.w("Failed else", "Registration Error individual.")

                    }

                }
            }
            .addOnFailureListener {
                def.complete(RegistrationSuccess(failed = it.message.toString()))

                Log.w("Error Register", it.message.toString())
            }
        return def.await()
    }


    fun logout() {
        /**
         * Log out user.
         */
        Firebase.auth.signOut()
    }

//============================================================END===================================================================================================================================================================================


}
