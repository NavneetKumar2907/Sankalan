package com.example.sankalan.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sankalan.R
import com.example.sankalan.data.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.concurrent.Executors

class AdminViewModel: ViewModel() {
    private val database = FirebaseDatabase.getInstance() // Database Instance
    private val databaseUser = database.getReference("Users")
    private val databaseEvent = database.getReference("Events") // Event Listener Reference

    private val teams = database.getReference("Teams")
    private val developerTeamReference = teams.child("Developers")
    private val panelTeamReference = teams.child("Panel")
    private val sponserReference = database.getReference("Sponsers")
    private val databaseRegisterEvent = database.getReference("RegisteredEvents")

    private val storage = Firebase.storage.reference
    val eventImageReference = storage.child("events")
    private val gallery_image_refrence = storage.child("gallery")
    private val sponserStoreag = storage.child("sponsers")


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
                    Log.w("Data","$res")
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

    private val userListener = object : ValueEventListener {
        /**
         * User Data Change Listener.
         */
        override fun onDataChange(snapshot: DataSnapshot) {
            val listuser = ArrayList<LoggedInUserView>()
            if(snapshot.exists()){
                for(users in snapshot.children){
                    val res = users.getValue<LoggedInUserView>()
                    res?.uid = users.key!!
                    listuser.add(res!!)
                }
                _userData.value = listuser
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error!", "Can't Retreive ${error.details}")
        }
    }

    private val regEventListener = object:ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.exists()){
                val eventLists = arrayListOf<RegisteredEvents>()
                for (eventName in snapshot.children){
                    for(id in eventName.children){
                        for (keys in id.children){
                            if(keys.hasChildren()){
                                for(teamName in keys.children){
                                    val res = teamName.getValue<TeamMembers>()
                                    eventLists.add(
                                        RegisteredEvents(
                                            teamName = teamName.key.toString(),
                                            members = res!!,
                                            eventName = eventName.key.toString()
                                        )
                                    )
                                }
                            }else{
                                //individual events
                                eventLists.add(
                                    RegisteredEvents(
                                        eventName = eventName.key.toString(),
                                        individual = keys.value.toString()
                                    )
                                )
                            }
                        }
                    }
                }

                registeredEvent.value = eventLists
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error",error.message)
        }

    }
    //UserLiveData
    private val _userData:MutableLiveData<ArrayList<LoggedInUserView>> by lazy {
        MutableLiveData<ArrayList<LoggedInUserView>>().also {
            loadUser()
        }
    }
    val userData:LiveData<ArrayList<LoggedInUserView>> = _userData

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
    private val registeredEvent:MutableLiveData<ArrayList<RegisteredEvents>> by lazy {
        MutableLiveData<ArrayList<RegisteredEvents>>().also {
            loadReg()
        }
    }
    val regEvent:LiveData<ArrayList<RegisteredEvents>> = registeredEvent

    private val images :MutableLiveData<ArrayList<Bitmap>> by lazy {
        MutableLiveData<ArrayList<Bitmap>>().also {
            viewModelScope.launch {
                loadGallery()
            }
        }
    }
    val imagesLive:LiveData<ArrayList<Bitmap>> = images

    val sponserImages:MutableLiveData<ArrayList<Bitmap>> by lazy {
        MutableLiveData<ArrayList<Bitmap>>().also {
            loadSponsers()
        }
    }

    fun loadSponsers(){
     //   sponserReference.list
    }
    private fun loadEvent() {
        /**
         * Load Events.
         */
        databaseEvent.addValueEventListener(eventListener)
    }
    private fun loadUser(){
        databaseUser.addValueEventListener(userListener)
    }
    private fun loadReg(){
        databaseRegisterEvent.addValueEventListener(regEventListener)
    }

    private fun loadGallery(){
        val temp = arrayListOf<Bitmap>()
        gallery_image_refrence.listAll()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    it.result.items.forEach {sto->
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
                                        images.value = temp
                                    }
                                }// end finally
                            }// edn executor

                        }// download
                    }
                }
            }
    }

    suspend fun editUser(data:LoggedInUserView):Upload{
        val def = CompletableDeferred<Upload>()
        databaseUser.child(data.uid).removeValue()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    def.complete(Upload(Sucess = R.string.Success))
                }else{
                    def.complete(Upload(failed = "Failed ${it.exception?.message}"))
                }
            }
        return def.await()
    }

    // Edit Event
    suspend fun editEvent(event:Events, eventName:String?=null): Upload {
        val def = CompletableDeferred<Upload>()
        val bitmap:Bitmap? = event.image_drawable
        var data:ByteArray? = null
        if(bitmap!=null){
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            data = baos.toByteArray()
        }
        if(data!=null){
            val filepath = eventImageReference.child(event.eventName)
            var uploadTask = filepath.putBytes(data)
            uploadTask.addOnCompleteListener{
                if(it.isSuccessful){
                    //Successful uploaded
                    filepath.downloadUrl
                        .addOnSuccessListener {
                            Log.w("URL",it.toString())
                            event.Image = it.toString()
                            def.complete(Upload(Sucess = R.string.uploadSucces))
                        }
                        .addOnFailureListener {
                            def.complete(Upload(failed = "Failed Upload ${it.message}"))
                        }
                }else{
                    //Failed Upload
                    Log.w("Failed Upload",it.exception.toString())
                    def.complete(Upload(failed = "Failed Upload ${it.exception?.message}"))
                }
            }
        }
        if(eventName!=null){
            deleteEvent(eventName)
        }
        def.await()
        event.image_drawable = null
        databaseEvent.child(event.eventName).setValue(event)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    def.complete(Upload(Sucess = R.string.uploadSucces))
                }else{
                    def.complete(Upload(failed = "Failed Updating Events. ${it.exception?.message}"))
                }
            }
        return def.await()
    }
    //Delete Event
    suspend fun deleteEvent(eventName:String): DeleteResult {
        val def = CompletableDeferred<DeleteResult>()
        eventImageReference.child(eventName).delete()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Log.w("ImageSucess","Delted Success.")
                }else{
                    Log.w("Failed","Image Delete Failed.")
                }
            }
        databaseEvent.child(eventName).removeValue()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    def.complete(DeleteResult(success = R.string.deleteSuccess))
                }else{
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
        try{
           for(e in eventList.value!!){
               val res = deleteEvent(e.eventName)
               if(res.failed==null){
                   count+=1
               }
           }
            if(count==size){
                def.complete(DeleteResult(success = R.string.deleteSuccess))
                eventList.value=ArrayList<Events>()
            }else{
                def.complete(DeleteResult(failed = "Some File Does Not Deleted."))
            }
       }catch (e:Exception){
           Log.w("Error",e.message.toString())
            def.complete(DeleteResult(failed = "Error in deleteding File. ${e.message}"))
       }
        return def.await()
    }

    fun uploadImages(bitmapList:List<Bitmap>){
        viewModelScope.launch {
            Log.w("ImageData","$bitmapList")
            for(data in bitmapList){
                var filepath:StorageReference

                filepath = gallery_image_refrence.child(data.toString())

                var bitmapByteArray:ByteArray? = null
                val baos = ByteArrayOutputStream()
                data.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                bitmapByteArray = baos.toByteArray()

                val uploadTask = filepath.putBytes(bitmapByteArray)
                uploadTask.addOnCompleteListener{
                    if(it.isSuccessful){
                        //Successful uploaded
                        Log.w("Uploadeed","Success")
                        loadGallery()
                    }else{
                        //Failed Upload
                        Log.w("Failed Upload",it.exception.toString())
                    }
                }//end upload
            }//end all bitmap


        }
    }

    fun deleteAll(){
        viewModelScope.launch {
            gallery_image_refrence.listAll()
                .addOnSuccessListener {
                    it.items.forEach {
                        it.delete()
                            .addOnSuccessListener {
                                Log.w("Deleted","Success")
                                loadGallery()

                            }
                            .addOnFailureListener{
                                Log.w("Deleted","Failed ${it.message}")
                            }
                    }
                }


        }
    }

    fun logout(){
        Firebase.auth.signOut()
    }


}