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
import kotlinx.coroutines.launch
import java.net.URL
import java.util.concurrent.Executors

class MainViewModel : ViewModel() {

    private val user: FirebaseUser? = Firebase.auth.currentUser // Current User
    private val database = FirebaseDatabase.getInstance() // Database Instance


//========================================Database References=========================================================================================================================================================================================

    // Database References
    private val timetable = database.getReference("timetable")
    private val databaseUser =
        database.getReference("Users").child(user?.uid.toString()) // User Reference
    private val image_ref = Firebase.storage.reference.child("gallery") // Gallery Reference
    private val databaseEvent = database.getReference("Events") // Event Listener Reference
    private val databaseRegisterEvent =
        database.getReference("RegisteredEvents") // Registered Event Reference

    private val teams = database.getReference("Teams")
    private val developerTeamReference = teams.child("Developers")
    private val panelTeamReference = teams.child("Panel")
    private val sponserReference = database.getReference("Sponsers")

    private val teamNameRefrence = database.getReference("teamName")
    private val resultReference = database.getReference("Result")

//=========================================Listeners========================================================================================================================================================================================


    private val userListener = object : ValueEventListener {
        /**
         * User Data Change Listener.
         */
        override fun onDataChange(snapshot: DataSnapshot) {
            val post = snapshot.getValue<LoggedInUserView>()
            try {
                if (post?.uid?.isEmpty() == true) {
                    post.uid = user?.uid.toString()
                    editUserDetail(post)
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
                val memList = ArrayList<TeamName>()
                for (eventName in snapshot.children) {
                    val res = TeamName(eventName = eventName.key.toString())
                    for (id in eventName.children) {
                        res.teamName.add(id.getValue<String>()!!)
                    }
                    memList.add(res)
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
                for (id in eventName.children) {


                    if (id.key?.lowercase() == emailFormatted.lowercase()) {

                        for (k in id.children) {
                            if (k.hasChildren()) {
                                // Team
                                for (teamName in k.children) {
                                    val res = teamName.getValue<TeamMembers>()

                                    eventMember.add(
                                        RegisteredEvents(
                                            eventName = eventName.key.toString(),
                                            members = res!!,
                                            teamName = teamName.key.toString()
                                        )//Registered Event Object
                                    )//added object to list
                                }//For Loop for teamName

                            } else {
                                // Individual
                                val res = k.value
                                eventMember.add(
                                    RegisteredEvents(
                                        eventName = eventName.key.toString(),
                                        individual = res.toString()
                                    )//Registered Event Object
                                )//added object to list
                            }
                        }//uid
                    }//email formatted condition check
                }//email formatted loop
            }//end data fetch
            _eventWiseMember.value = eventMember
        }//end function

        override fun onCancelled(error: DatabaseError) {
            Log.w("ERROR", error.message)
        }

    }

    private val developerListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val listDeveloper = arrayListOf<Teams>()
                for (names in snapshot.children) {

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
                    }//End Executor
                }//End Loop
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

    //Result Listener
    private val resultListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                //Result is there
                val resultList = arrayListOf<Score>()
                for (eventName in snapshot.children) {
                    val res =Score(eventName = eventName.key.toString())
                    for(id in eventName.children){
                        id.getValue<String>()?.let { res.result.add(it) }
                    }
                    resultList.add(res)
                }//End Loop
                //add value to live data
                scoreLive.value = resultList
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error:", "DATA NOT FOUND!!")
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

    private val teamNameLive: MutableLiveData<ArrayList<TeamName>> by lazy {
        MutableLiveData<ArrayList<TeamName>>().also {
            loadList()
        }
    }

    private val scoreLive: MutableLiveData<ArrayList<Score>> by lazy {
        MutableLiveData<ArrayList<Score>>().also {
            loadScore()
        }
    }
    val liveResult: LiveData<ArrayList<Score>> = scoreLive

    val liveTimeTable:MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
            timetable.get().addOnCompleteListener {task->
                it.value = task.result.getValue<String>()
            }
        }
    }


//=====================================Loaders============================================================================================================================================================================================




    private fun loadList() {
        teamNameRefrence.addValueEventListener(teamMemberListener)
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
                        var imageL: Bitmap? = null
                        executer.execute {
                            try {
                                val `in` = URL(u.toString()).openStream()
                                imageL = BitmapFactory.decodeStream(`in`)
                            } catch (e: Exception) {
                                Log.w("Image Error", e.message.toString())
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

    private fun loadScore() {
        resultReference.addValueEventListener(resultListener)
    }


//====================================Functionality Functions=============================================================================================================================================================================================

    fun editUserDetail(newDetail: LoggedInUserView) {
        /**
         * Edit user Details
         */
        try {
            newDetail.email = user?.email.toString()
            newDetail.uid = user?.uid.toString()
            newDetail.isVerified = user?.isEmailVerified == true
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
        val aboutEvent = eventList.value?.find {
            it.eventName == eventName
        }
        if (team) {
            // Check if user is registered.
            members.apply {
                //Check Member registration
                if(aboutEvent?.teamSize == 2){
                    //Two Member
                    //Check If Registered
                    if (member2.isNotEmpty() && !isRegistered(member2)) {
                        def.complete(RegistrationSuccess(failed = "Member 2 is Not Registered."))
                        return def.await()
                    }
                }else{
                    //Four Member
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
                }

                if (teamName.isEmpty()) {
                    def.complete(RegistrationSuccess(failed = "Team Name Required."))
                    return def.await()
                }
                else {
                    //Check if team Already Exist
                    val v = teamNameLive.value?.filter {
                        it.eventName == eventName
                    }
                    if (v != null) {
                        if (v.isNotEmpty() && v[0].teamName.contains(teamName)) {
                            def.complete(RegistrationSuccess(failed = "Team Name Already Exist."))
                            return def.await()
                        }
                    }
                }
            }

            // check if members are already registered For the Event
            members.apply {

                if(aboutEvent?.teamSize == 2){
                    //Two Member Event
                    if (member2.isNotEmpty()) {
                        databaseRegisterEvent.child(eventName)
                            .child(member2.replace("@", "at").replace(".", "dot"))
                            .get()
                            .addOnCompleteListener {
                                if (it.result.hasChildren()) {
                                    def.complete(RegistrationSuccess(failed = "$member2 is already registered in another team."))
                                }
                            }
                        if(def.isCompleted){
                            return def.await()
                        }

                    }
                }else{
                    //Four member
                    if (member2.isNotEmpty()) {
                        databaseRegisterEvent.child(eventName)
                            .child(member2.replace("@", "at").replace(".", "dot"))
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
                        databaseRegisterEvent.child(eventName)
                            .child(member3.replace("@", "at").replace(".", "dot"))
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
                        databaseRegisterEvent.child(eventName)
                            .child(member2.replace("@", "at").replace(".", "dot"))
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
                    }//End Member 4
                    if(def.isCompleted){
                        return def.await()
                    }
                }//End if else

            }//End Apply


                // add team name
                teamNameRefrence.child(eventName).push().setValue(teamName)

                databaseRegisterEvent.child(eventName)
                    .child(user?.email.toString().replace("@", "at").replace(".", "dot")).push()
                    .child(teamName).setValue(members)
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

                }//end apply
                def.complete(RegistrationSuccess(success = R.string.sucess_register))
                return def.await()

        } else {
            databaseRegisterEvent.child(eventName)
                .child(user?.email.toString().replace("@", "at").replace(".", "dot")).push()
                .setValue(user?.uid)
            def.complete(RegistrationSuccess(success = R.string.sucess_register))
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

        databaseRegisterEvent.child(eventName)
            .child(user?.email.toString().replace("@", "at").replace(".", "dot")).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    //Already Registered for same events
                    def.complete(RegistrationSuccess(failed = "Already Registered"))
                } else {
                    //Register
                    if (teamNameLive.value?.find {
                            it.eventName == eventName && it.teamName.contains(teamName)
                        } != null) {
                        // Team Name Already Exist
                        def.complete(RegistrationSuccess(failed = "Team Name ALready Exist."))
                    } else {
                        viewModelScope.launch {
                            def.complete(
                                register(
                                    team,
                                    members,
                                    eventName,
                                    teamName.trim().lowercase()
                                )
                            )
                        }
                    }


                }//End Else
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
        viewModelScope.launch {
            Firebase.auth.signOut()
        }
    }


//============================================================END===================================================================================================================================================================================



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
