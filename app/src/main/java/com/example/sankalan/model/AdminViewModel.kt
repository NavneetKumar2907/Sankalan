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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*
import java.util.concurrent.Executors

class AdminViewModel : ViewModel() {
//========================================Database References=========================================================================================================================================================================================

    private val database = FirebaseDatabase.getInstance() // Database Instance
    private val databaseUser = database.getReference("Users")
    private val databaseEvent = database.getReference("Events") // Event Listener Reference

    private val teams = database.getReference("Teams")
    private val developerTeamReference = teams.child("Developers")
    private val panelTeamReference = teams.child("Panel")
    private val sponserReference = database.getReference("Sponsers")

    private val databaseRegisterEvent = database.getReference("RegisteredEvents")

    private val storage = Firebase.storage.reference
    private val eventImageReference = storage.child("events")
    private val galleryImageReference = storage.child("gallery")
    private val sponserStorage = storage.child("teams").child("sponsers")
    private val developerStorage = storage.child("teams").child("developers")
    private val panelStorage = storage.child("teams").child("panels")

//=========================================Listeners========================================================================================================================================================================================

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
                    val executor = Executors.newSingleThreadExecutor()
                    val handler = Handler(Looper.getMainLooper())
                    var image: Bitmap?
                    Log.w("Data", "$res")
                    executor.execute {
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
                            }//End Handler
                        }//End Try,catch,finally
                    }//End Executor
                }


            } else {
                Log.w("Error", "NOT found data.")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error", error.details)
        }

    }

    private val userListener = object : ValueEventListener {
        /**
         * User Data Change Listener.
         */
        override fun onDataChange(snapshot: DataSnapshot) {
            val listUser = ArrayList<LoggedInUserView>()
            if (snapshot.exists()) {
                for (users in snapshot.children) {
                    val res = users.getValue<LoggedInUserView>()
                    res?.uid = users.key!!
                    listUser.add(res!!)
                }
                _userData.value = listUser
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error!", "Can't Retreive ${error.details}")
        }
    }

    private val regEventListener = object : ValueEventListener {
        /**
         * Registered Event Listener.
         */
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val eventLists = arrayListOf<RegisteredEvents>()
                for (eventName in snapshot.children) {
                    for (id in eventName.children) {
                        for (keys in id.children) {

                            if (keys.hasChildren()) {
                                for (teamName in keys.children) {
                                    val res = teamName.getValue<TeamMembers>()

                                    eventLists.add(
                                        RegisteredEvents(
                                            teamName = teamName.key.toString(),
                                            members = res!!,
                                            eventName = eventName.key.toString()
                                        )//Registered Event Object
                                    )//Added to list
                                }//End Teamname Loop
                            } else {
                                //individual events
                                eventLists.add(
                                    RegisteredEvents(
                                        eventName = eventName.key.toString(),
                                        individual = keys.value.toString()
                                    )//RegisteredEvent Object
                                )//add TO list
                            }//end if else
                        }//end key loop
                    }//end id loop
                }//end eventName loop

                registeredEvent.value = eventLists
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error", error.message)
        }

    }

    private val developerListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                //Data Is Available
                val listDeveloper = arrayListOf<Teams>()

                for (developerName in snapshot.children) {
                    val res = developerName.getValue<Teams>()

                    //Set Up Image
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
                                listDeveloper.add(res!!)
                                developerTeam.value = listDeveloper
                            }//end handler
                        }//end finally
                    }//end executer

                }//End Developer Name as Key
            } else {
                Log.w("Error", "NOT found data.")
            }
        }//End Function

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error Team", error.message)
        }

    }

    private val panelListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val listPanel = arrayListOf<Teams>()
                for (names in snapshot.children) {
                    val res = names.getValue<Teams>()
                    val executor = Executors.newSingleThreadExecutor()
                    val handler = Handler(Looper.getMainLooper())
                    var img: Bitmap?
                    executor.execute {
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
                            }//end Handler
                        }//End try finally
                    }//End Executor
                }
            } else {
                Log.w("Error", "NOT found data.")

            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error", error.details)
        }
    }

//=========================================Live Data========================================================================================================================================================================================

    //UserLiveData
    private val _userData: MutableLiveData<ArrayList<LoggedInUserView>> by lazy {
        MutableLiveData<ArrayList<LoggedInUserView>>().also {
            loadUser()
        }
    }
    val userData: LiveData<ArrayList<LoggedInUserView>> = _userData

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

    // Registered Event Live Data
    private val registeredEvent: MutableLiveData<ArrayList<RegisteredEvents>> by lazy {
        MutableLiveData<ArrayList<RegisteredEvents>>().also {
            loadReg()
        }
    }
    val regEvent: LiveData<ArrayList<RegisteredEvents>> = registeredEvent

    private val images: MutableLiveData<ArrayList<Bitmap>> by lazy {
        MutableLiveData<ArrayList<Bitmap>>().also {
            viewModelScope.launch {
                loadGallery()
            }
        }
    }
    val imagesLive: LiveData<ArrayList<Bitmap>> = images

    private val developerTeam: MutableLiveData<ArrayList<Teams>> by lazy {
        MutableLiveData<ArrayList<Teams>>().also {
            loadDeveloper()
        }
    }

    val DeveloperTeam: LiveData<ArrayList<Teams>> = developerTeam

    private val panelTeam: MutableLiveData<ArrayList<Teams>> by lazy {
        MutableLiveData<ArrayList<Teams>>().also {
            loadPanel()
        }
    }
    val livePanelTeam: LiveData<ArrayList<Teams>> = panelTeam


    val sponserImages: MutableLiveData<ArrayList<Bitmap>> by lazy {
        MutableLiveData<ArrayList<Bitmap>>().also {
            loadSponsers()
        }
    }
//=========================================Loaders========================================================================================================================================================================================

    fun loadSponsers() {
        //   sponserReference.list
    }

    private fun loadEvent() {
        /**
         * Load Events.
         */
        databaseEvent.addValueEventListener(eventListener)
    }

    private fun loadUser() {
        databaseUser.addValueEventListener(userListener)
    }

    private fun loadReg() {
        databaseRegisterEvent.addValueEventListener(regEventListener)
    }

    private fun loadGallery() {
        val temp = arrayListOf<Bitmap>()
        galleryImageReference.listAll()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.items.forEach { sto ->
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
                                        images.value = temp
                                    }
                                }// end finally
                            }// edn executor

                        }// download
                    }
                }
            }
    }

    private fun loadDeveloper() {
        developerTeamReference.addValueEventListener(developerListener)
    }

    private fun loadPanel() {
        panelTeamReference.addValueEventListener(panelListener)
    }

    suspend fun editUser(data: LoggedInUserView): Upload {
        val def = CompletableDeferred<Upload>()
        databaseUser.child(data.uid).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    def.complete(Upload(Success = R.string.Success))
                } else {
                    def.complete(Upload(failed = "Failed ${it.exception?.message}"))
                }
            }
        return def.await()
    }
//=========================================Function========================================================================================================================================================================================

    // Edit Event
    suspend fun editEvent(event: Events, eventName: String? = null): Upload {
        val def = CompletableDeferred<Upload>()
        val bitmap: Bitmap? = event.image_drawable
        var data: ByteArray? = null
        if (bitmap != null) {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            data = baos.toByteArray()
        }
        if (data != null) {
            val filepath = eventImageReference.child(event.eventName)
            val uploadTask = filepath.putBytes(data)
            uploadTask.addOnCompleteListener {
                if (it.isSuccessful) {
                    //Successful uploaded
                    filepath.downloadUrl
                        .addOnSuccessListener {
                            Log.w("URL", it.toString())
                            event.Image = it.toString()
                            def.complete(Upload(Success = R.string.uploadSucces))
                        }
                        .addOnFailureListener {
                            def.complete(Upload(failed = "Failed Upload ${it.message}"))
                        }
                } else {
                    //Failed Upload
                    Log.w("Failed Upload", it.exception.toString())
                    def.complete(Upload(failed = "Failed Upload ${it.exception?.message}"))
                }
            }
        }
        if (eventName != null) {
            deleteEvent(eventName)
        }
        def.await()
        event.image_drawable = null
        databaseEvent.child(event.eventName).setValue(event)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    def.complete(Upload(Success = R.string.uploadSucces))
                } else {
                    def.complete(Upload(failed = "Failed Updating Events. ${it.exception?.message}"))
                }
            }
        return def.await()
    }

    //Delete Event
    suspend fun deleteEvent(eventName: String): DeleteResult {
        val def = CompletableDeferred<DeleteResult>()
        eventImageReference.child(eventName).delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.w("ImageSucess", "Delted Success.")
                } else {
                    Log.w("Failed", "Image Delete Failed.")
                }
            }
        databaseEvent.child(eventName).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    def.complete(DeleteResult(success = R.string.deleteSuccess))
                } else {
                    def.complete(DeleteResult(failed = "Failed Delete ${it.exception?.message}"))
                }
            }
        return def.await()
    }

    //delete all event
    suspend fun deleteAllEvent(): DeleteResult {
        val def = CompletableDeferred<DeleteResult>()
        val size = eventList.value?.size
        var count = 0
        try {
            for (e in eventList.value!!) {
                val res = deleteEvent(e.eventName)
                if (res.failed == null) {
                    count += 1
                }
            }
            if (count == size) {
                def.complete(DeleteResult(success = R.string.deleteSuccess))
                eventList.value = ArrayList<Events>()
            } else {
                def.complete(DeleteResult(failed = "Some File Does Not Deleted."))
            }
        } catch (e: Exception) {
            Log.w("Error", e.message.toString())
            def.complete(DeleteResult(failed = "Error in deleting File. ${e.message}"))
        }
        return def.await()
    }

    fun uploadImages(bitmapList: List<Bitmap>) {
        viewModelScope.launch {
            Log.w("ImageData", "$bitmapList")
            for (data in bitmapList) {

                val filepath: StorageReference = galleryImageReference.child(data.toString())

                var bitmapByteArray: ByteArray?
                val baos = ByteArrayOutputStream()
                data.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                bitmapByteArray = baos.toByteArray()

                val uploadTask = filepath.putBytes(bitmapByteArray)
                uploadTask.addOnCompleteListener {
                    if (it.isSuccessful) {
                        //Successful uploaded
                        Log.w("Uploaded", "Success")
                        loadGallery()
                    } else {
                        //Failed Upload
                        Log.w("Failed Upload", it.exception.toString())
                    }
                }//end upload
            }//end all bitmap


        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            galleryImageReference.listAll()
                .addOnSuccessListener {
                    it.items.forEach {
                        it.delete()
                            .addOnSuccessListener {
                                Log.w("Deleted", "Success")
                                loadGallery()

                            }
                            .addOnFailureListener {
                                Log.w("Deleted", "Failed ${it.message}")
                            }
                    }
                }


        }
    }

    suspend fun editMember(values: Teams, imageChanged: Boolean = false): Upload {

        val filepath: StorageReference? //File Path

        val smalName: String = values.name.trim().split("\\s+".toRegex())[0].lowercase(
            Locale.getDefault()
        ) //Name To be stored as child
        filepath = if (values.position.isNotEmpty()) {
            //Panel Team
            panelStorage.child(smalName)
        } else {
            //Developer Team
            developerStorage.child(smalName)
        }
        // Upload Image

        val def = CompletableDeferred<Upload>()
        if (imageChanged) {

            val baos = ByteArrayOutputStream()
            values.imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val uploadTask = filepath.putBytes(data)
            uploadTask.addOnCompleteListener {
                if (it.isSuccessful) {
                    //Successful uploaded
                    filepath.downloadUrl
                        .addOnSuccessListener {
                            Log.w("URL", it.toString())
                            values.image = it.toString()
                            //Set Up Real Time Data
                            values.imageBitmap = null
                            if (values.position.isNotEmpty()) {
                                panelTeamReference.child(smalName).setValue(values)

                            } else {
                                developerTeamReference.child(smalName).setValue(values)
                            }
                            def.complete(Upload(Success = R.string.uploadSucces))
                        }
                        .addOnFailureListener {
                            def.complete(Upload(failed = "Failed Upload ${it.message}"))
                        }
                } else {
                    //Failed Upload
                    Log.w("Failed Upload", it.exception.toString())
                    def.complete(Upload(failed = "Failed Upload ${it.exception?.message}"))
                }
            }
        } else {
            //No IMage Change
            values.imageBitmap = null
            if (values.position.isNotEmpty()) {
                panelTeamReference.child(smalName).setValue(values)

            } else {
                developerTeamReference.child(smalName).setValue(values)
            }
            def.complete(Upload(Success = R.string.uploadSucces))
        }
        return def.await()
    }

    fun logout() {
        viewModelScope.launch {
            Firebase.auth.signOut()
        }
    }


}