package com.example.sankalan.dialogfragments

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.viewModelScope
import com.example.sankalan.R
import com.example.sankalan.activities.adminViewModel
import com.example.sankalan.data.Events
import com.example.sankalan.databinding.AddEventBinding
import kotlinx.coroutines.launch

class AddEvent(val editEvent: Events?=null):DialogFragment() {

    lateinit var addEventBinding: AddEventBinding
    var imageUri: Uri?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addEventBinding = AddEventBinding.inflate(inflater)
        return addEventBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterType = ArrayAdapter.createFromResource(requireContext(),R.array.eventType,android.R.layout.simple_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            addEventBinding.typeSpinner.adapter = it
        }
        val adapterTeam = ArrayAdapter.createFromResource(requireContext(),R.array.team,android.R.layout.simple_spinner_item).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            addEventBinding.teamSpinner.adapter = it
        }

            addEventBinding.apply {
                addeventName.setText( editEvent?.eventName)
                addeventDescription.setText(editEvent?.Description)
                addeventRules.setText(editEvent?.rules)
                typeSpinner.setSelection(adapterType.getPosition(editEvent?.Type))
                teamSpinner.setSelection(adapterTeam.getPosition(editEvent?.Team))
                addeventVenue.setText(editEvent?.Venue)
                addCoordinator.setText(editEvent?.Coordinator)
                eventImage.setImageBitmap(editEvent?.image_drawable)
                try{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePicker.hour = editEvent?.timeHour!!
                        timePicker.minute = editEvent?.timeMinute!!

                    }else{
                        timePicker.currentHour = editEvent?.timeHour!!
                        timePicker.currentMinute = editEvent?.timeMinute!!

                    }

                }catch (e:Exception){
                    Log.w("Error Time",e.message.toString())
                }
            }

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){
            imageUri = it
            addEventBinding.eventImage.setImageURI(it)
        }

        addEventBinding.browseImage.setOnClickListener {
            // Browse Image File
            getContent.launch("image/*")
        }

        addEventBinding.submitAdd.setOnClickListener {
            addEventBinding.loading.visibility = View.VISIBLE
            try{
               val eventAdd =  Events(
                    eventName = addEventBinding.addeventName.text.toString(),
                    Type = addEventBinding.typeSpinner.selectedItem.toString(),
                    Team = addEventBinding.teamSpinner.selectedItem.toString(),
                    Venue = addEventBinding.addeventVenue.text.toString(),
                    timeHour =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) addEventBinding.timePicker.hour else addEventBinding.timePicker.currentHour,
                    timeMinute =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) addEventBinding.timePicker.minute else addEventBinding.timePicker.currentMinute,
                    Coordinator = addEventBinding.addCoordinator.text.toString(),
                    Description = addEventBinding.addeventDescription.text.toString(),
                    rules = addEventBinding.addeventRules.text.toString()

                )
                //get Image
                if(imageUri!=null){
                    val bm = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri)
                    eventAdd.image_drawable = bm
                }else{
                    val bitmapDrawable:BitmapDrawable = addEventBinding.eventImage.drawable as BitmapDrawable
                    eventAdd.image_drawable = bitmapDrawable.bitmap
                }

                adminViewModel.viewModelScope.launch {
                    val res = adminViewModel.editEvent(eventAdd,eventAdd.eventName)
                    if(res.Sucess!=null){
                        Handler(Looper.getMainLooper()).post {
                            addEventBinding.loading.visibility = View.GONE
                            dialog?.dismiss()
                        }
                    }else{
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context,res.failed,Toast.LENGTH_SHORT).show()
                            addEventBinding.loading.visibility = View.GONE
                        }
                    }
                }
            }catch (e:Exception){
                Toast.makeText(context,"Error in Uploading. Require All Input.",Toast.LENGTH_SHORT).show()
                addEventBinding.loading.visibility = View.GONE
            }

        }

        addEventBinding.cancelAdd.setOnClickListener {
            dialog?.cancel()
        }
    }


}