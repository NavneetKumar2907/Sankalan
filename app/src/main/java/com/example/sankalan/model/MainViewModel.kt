package com.example.sankalan.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sankalan.R
import com.example.sankalan.data.*
import com.example.sankalan.ui.developers.Teams
import com.example.sankalan.ui.login.data.LoggedInUser
import com.example.sankalan.ui.sponsers.Sponsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.net.URL
import java.util.concurrent.Executors

class MainViewModel : ViewModel() {

    private val user: FirebaseUser? = Firebase.auth.currentUser // Current User
    private val database = FirebaseDatabase.getInstance() // Database Instance
//========================================Database References=========================================================================================================================================================================================

    // Database References
    private val databaseUser =
        database.getReference("Users").child(user?.uid.toString()) // User Reference
    private val image_ref = Firebase.storage.reference.child("gallery") // Gallery Reference
    private val databaseEvent = database.getReference("Events") // Event Listener Reference
    private val databaseRegisterEvent = database.getReference("RegisteredEvents") // Registered Event Reference

    private val teams = database.getReference("Teams")
    private val developerTeamReference = teams.child("Developers")
    private val panelTeamReference = teams.child("Panel")
    private val sponserReference = database.getReference("Sponsers")

    private val teamMembersReference = database.getReference("teamName")

//=========================================Listeners========================================================================================================================================================================================

    private val userListener = object : ValueEventListener {
        /**
         * User Data Change Listener.
         */
        override fun onDataChange(snapshot: DataSnapshot) {
            val post = snapshot.getValue<LoggedInUserView>()
            Log.w("Post Value", "${post}")
            try {
                post?.uid = user?.uid!!
                if (!user.isEmailVerified) {
                    post?.isVerified = user.isEmailVerified
                    databaseUser.setValue(post)
                }
                _userData.value = post
            } catch (e: Exception) {
                Log.w("Error Loading details", e.message.toString())
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error!", "Can't Retreive ${error.details}")
        }
    }

    private val teamMemberListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val memList = ArrayList<String>()
                for (teamName in snapshot.children) {
                    memList.add(teamName.toString())
                }
                teamNameLive.value = memList
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
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
                    val res = childEventname.getValue<Events>()!!
                    //Setting Image with url
                    val executer = Executors.newSingleThreadExecutor()
                    val handler = Handler(Looper.getMainLooper())
                    var image: Bitmap?

                    executer.execute {
                        try {
                            val `in` = URL(res.Image).openStream()
                            image = BitmapFactory.decodeStream(`in`)
                            res.image_drawable = image
                        } catch (e: Exception) {
                            Log.w("Image Error", e.message.toString() + res.Image)
                        } finally {
                            handler.post {
                                list.add(res)
                                eventList.value = list
                            }
                        }
                    }
                }


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
            val emailFormatted = user?.email.toString().replace("@", "at").replace(".", "dot")
            for (eventName in snapshot.children) {
                for(id in eventName.children){
                    if(id.key == emailFormatted){
                        for(k in id.children){
                            if(k.hasChildren()){
                                // Team
                                    for(teamName in k.children){
                                        val res = teamName.getValue<TeamMembers>()
                                        eventMember.add(
                                            RegisteredEvents(
                                                eventName = eventName.key.toString(),
                                                members = res!!,
                                                teamName = teamName.key.toString()
                                            )
                                        )
                                    }

                            }else{
                                // Individual
                                val res = k.getValue()
                                eventMember.add(
                                    RegisteredEvents(
                                        eventName = eventName.key.toString(),
                                        individual = res.toString()
                                    )
                                )
                            }
                        }
                    }
                }
            }
            _eventWiseMember.value = eventMember
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("ERROR", error.message)
        }

    }
    private val developerListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val listDeveloper = arrayListOf<Teams>()
                for (names in snapshot.children) {
                    Log.w("v","$names")
                    val res = names.getValue<Teams>()
                    val executer = Executors.newSingleThreadExecutor()
                    val handler = Handler(Looper.getMainLooper())
                    var img: Bitmap? = null
                    executer.execute {
                        try {
                            val `in` = java.net.URL(res?.image).openStream()
                            img = BitmapFactory.decodeStream(`in`)
                            res?.imageBitmap = img
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            handler.post {
                                listDeveloper.add(res!!)
                                developerTeam.value = listDeveloper
                            }
                        }
                    }
                }
            } else {
                Log.w("Error", "NOT found data.")

            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error", error.details)
        }

    }

    private val panelListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val listPanel = arrayListOf<Teams>()
                for (names in snapshot.children) {
                    val res = names.getValue<Teams>()
                    val executer = Executors.newSingleThreadExecutor()
                    val handler = Handler(Looper.getMainLooper())
                    var img: Bitmap?
                    executer.execute {
                        try {
                            val `in` = java.net.URL(res?.image).openStream()
                            img = BitmapFactory.decodeStream(`in`)
                            res?.imageBitmap = img
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            handler.post {
                                listPanel.add(res!!)
                                panelTeam.value = listPanel
                            }
                        }
                    }
                }
            } else {
                Log.w("Error", "NOT found data.")

            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error", error.details)
        }
    }

    // Sponser listener
    private val sponserListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val listSponsers = arrayListOf<Sponsers>()
                for (sponsers in snapshot.children) {
                    val res = sponsers.getValue<Sponsers>()
                    val executer = Executors.newSingleThreadExecutor()
                    val handler = Handler(Looper.getMainLooper())
                    var img: Bitmap?
                    executer.execute {
                        try {
                            val `in` = java.net.URL(res?.image).openStream()
                            img = BitmapFactory.decodeStream(`in`)
                            res?.imageBitmap = img
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            handler.post {
                                listSponsers.add(res!!)
                                sponsersLive.value = listSponsers
                            }
                        }
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error", "Error in Getting sponser data $error")
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
    private val _images_gallery: MutableLiveData<ArrayList<Bitmap>> by lazy {
        MutableLiveData<ArrayList<Bitmap>>().also {
            loadImages()
        }
    }
    val images_gallery: LiveData<ArrayList<Bitmap>> =
        _images_gallery // Getter of gallery image lists

    //Registered Event Live Data
    private val _eventWiseMember: MutableLiveData<ArrayList<RegisteredEvents>> by lazy {
        MutableLiveData<ArrayList<RegisteredEvents>>().also {
            loadRegisteredEvents()
        }
    }


    val eventWiseMember: LiveData<ArrayList<RegisteredEvents>> =
        _eventWiseMember // Getter of Registered Events

    private val developerTeam: MutableLiveData<ArrayList<Teams>> by lazy {
        MutableLiveData<ArrayList<Teams>>().also {
            loadDeveloper()
        }
    }
    val liveDeveloperTeam: LiveData<ArrayList<Teams>> = developerTeam

    private val panelTeam: MutableLiveData<ArrayList<Teams>> by lazy {
        MutableLiveData<ArrayList<Teams>>().also {
            loadPanel()
        }
    }
    val livePanelTeam: LiveData<ArrayList<Teams>> = panelTeam

    private val sponsersLive: MutableLiveData<ArrayList<Sponsers>> by lazy {
        MutableLiveData<ArrayList<Sponsers>>().also {
            loadSponsers()
        }
    }
    val liveSponser: LiveData<ArrayList<Sponsers>> = sponsersLive

    private val teamNameLive: MutableLiveData<ArrayList<String>> by lazy {
        MutableLiveData<ArrayList<String>>().also {
            loadList()
        }
    }

//=====================================Loaders============================================================================================================================================================================================

    private fun loadList() {
        teamMembersReference.addValueEventListener(teamMemberListener)
    }

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
        val temp = arrayListOf<Bitmap>()
        image_ref.listAll()
            .addOnSuccessListener {
                it.items.forEach { sto ->
                    sto.downloadUrl.addOnSuccessListener { u ->
                        val executer = Executors.newSingleThreadExecutor()
                        val handler = Handler(Looper.getMainLooper())
                        var imageL:Bitmap? = null
                        executer.execute {
                            try {
                                val `in` = URL(u.toString()).openStream()
                                imageL = BitmapFactory.decodeStream(`in`)
                            } catch (e: Exception) {
                                Log.w("Image Error", e.message.toString() )
                            } finally {
                                handler.post {
                                    temp.add(imageL!!)
                                }
                            }// end finally
                        }// edn executor

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

    private fun loadDeveloper() {
        developerTeamReference.addValueEventListener(developerListener)
    }

    private fun loadPanel() {
        panelTeamReference.addValueEventListener(panelListener)
    }

    private fun loadSponsers() {
        sponserReference.addValueEventListener(sponserListener)
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


    suspend fun isRegistered(email: String): Boolean {
        /**
         * Check Already Registered Email.
         */
        val def = CompletableDeferred<Boolean>()
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener {
                try {
                    if (it.result.signInMethods!!.isNotEmpty()) {
                        def.complete(true)
                    } else {
                        def.complete(false)
                    }
                } catch (e: Exception) {
                    def.complete(false)
                }
            }
            .addOnFailureListener {
                def.complete(false)
            }
        return def.await()
    }

    private suspend fun register(
        team: Boolean,
        members: TeamMembers,
        eventName: String,
        teamName: String = ""
    ): RegistrationSuccess {
        /**
         * Validation of Members email and registration.
         */
        val def = CompletableDeferred<RegistrationSuccess>()
        if (team) {
            // Check if user is registered.
            members.apply {

                if (member2.isNotEmpty() && !isRegistered(member2)) {
                    def.complete(RegistrationSuccess(failed = "Member 2 is Not Registered."))
                    return def.await()
                }
                if (member3.isNotEmpty() && !isRegistered(member3)) {
                    def.complete(RegistrationSuccess(failed = "Member 3 is Not Registered."))
                    return def.await()
                }
                if (member4.isNotEmpty() && !isRegistered(member4)) {
                    def.complete(RegistrationSuccess(failed = "Member 4 is Not Registered."))
                    return def.await()
                }
                if (teamName.isEmpty()) {
                    def.complete(RegistrationSuccess(failed = "Team Name Required."))
                }
            }
            // check if members are already registered
            members.apply {
                if (member2.isNotEmpty()) {
                    databaseRegisterEvent.child(eventName).child(member2.replace("@", "at").replace(".", "dot"))
                        .get()
                        .addOnCompleteListener {
                            if (it.result.hasChildren()) {
                                def.complete(RegistrationSuccess(failed = "$member2 is already registered in another team."))
                            } else {
                                def.complete(RegistrationSuccess())
                            }
                        }

                }
                if (member3.isNotEmpty()) {
                    databaseRegisterEvent.child(eventName).child(member3.replace("@", "at").replace(".", "dot"))
                        .get()
                        .addOnCompleteListener {
                            if (it.result.hasChildren()) {
                                def.complete(RegistrationSuccess(failed = "$member3 is already registered in another team."))
                            } else {
                                def.complete(RegistrationSuccess())
                            }
                        }
                }
                if (member4.isNotEmpty()) {
                    databaseRegisterEvent.child(eventName).child(member2.replace("@", "at").replace(".", "dot"))
                        .get()
                        .addOnCompleteListener {
                            if (it.result.hasChildren()) {
                                def.complete(RegistrationSuccess(failed = "$member4 is already registered in another team."))
                            } else {
                                try {
                                    def.complete(RegistrationSuccess(failed = null))
                                } catch (e: Exception) {
                                    Log.w("Error", e.message.toString())
                                }
                            }
                        }
                }
                if (teamName.isNotEmpty()) {
                    if (teamNameLive.value != null && teamNameLive.value!!.contains(teamName)) {
                        def.complete(RegistrationSuccess(failed = "Team Name Already Exist."))
                    }
                }
            }


            if (def.await().failed != null) {
                stuckLog("Inside active")
            } else {
                stuckLog("Inside Not active.")
                // add team name
                databaseRegisterEvent.child(eventName).child(user?.email.toString().replace("@", "at").replace(".", "dot")).push().child(teamName).setValue(members)
                if(teamNameLive.value==null){
                    teamMembersReference.setValue(arrayListOf(teamName))
                }else{
                    teamMembersReference.setValue(teamNameLive.value?.add(teamName))
                }
                members.apply {
                    if (member2.isNotEmpty()) {
                        uploadMemberValue(
                            member2.replace("@", "at").replace(".", "dot"),
                            eventName,
                            this,
                            teamName
                        )
                    }
                    if (member3.isNotEmpty()) {
                        uploadMemberValue(
                            member3.replace("@", "at").replace(".", "dot"),
                            eventName,
                            this,
                            teamName
                        )
                    }
                    if (member4.isNotEmpty()) {
                        uploadMemberValue(
                            member4.replace("@", "at").replace(".", "dot"),
                            eventName,
                            this,
                            teamName
                        )

                    }

                }
                def.complete(RegistrationSuccess(succes = R.string.sucess_register))
            }

        } else {
            databaseRegisterEvent.child(eventName).child(user?.email.toString().replace("@", "at").replace(".", "dot")).push().setValue(user?.uid)

            def.complete(RegistrationSuccess(succes = R.string.sucess_register))
        }
        return def.await()
    }


    suspend fun registerForEvent(
        eventName: String,
        team: Boolean = false,
        members: TeamMembers = TeamMembers(),
        teamName: String = ""
    ): RegistrationSuccess {
        /**
         * Register for Event.
         */
        val def = CompletableDeferred<RegistrationSuccess>()

        databaseRegisterEvent.child(eventName).child(user?.email.toString().replace("@", "at").replace(".", "dot")).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    //Already Registered for some events
                    if (it.hasChild(eventName)) {
                        def.complete(RegistrationSuccess(failed = "Already Registered"))
                        Log.w("Success", "Registration Already exist.")
                    } else {
                        // Not registered for this event
                        //Register
                        viewModelScope.launch {
                            val res = register(team, members, eventName, teamName)
                            Handler(Looper.getMainLooper()).post {
                                def.complete(res)
                            }
                            if (!def.isActive) {
                                this.cancel("Completed")
                            }
                        }
                    }

                } else {
                    //Register
                    viewModelScope.launch {
                        def.complete(register(team, members, eventName, teamName))
                        if (!def.isActive) {
                            this.cancel("Completed")
                        }
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

    fun stuckLog(m: String) {
        Log.w("Stuck", m)
    }

    fun
            uploadMemberValue(
        email: String,
        eventName: String,
        values: TeamMembers,
        teamName: String
    ) {
        databaseRegisterEvent.child(eventName).child(email).push().child(teamName).setValue(values)
    }
}
