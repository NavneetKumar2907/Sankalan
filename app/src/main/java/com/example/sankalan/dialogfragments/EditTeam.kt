package com.example.sankalan.dialogfragments

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.example.sankalan.R
import com.example.sankalan.data.Teams
import com.example.sankalan.databinding.EditTeamPanelBinding
import com.example.sankalan.model.AdminViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class EditTeam(var value:Teams, var pos:Boolean =false): DialogFragment() {
    lateinit var bindingteam:EditTeamPanelBinding
    val model by activityViewModels<AdminViewModel>()
    var imageChanged:Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingteam = EditTeamPanelBinding.inflate(layoutInflater)
        return bindingteam.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Setting up data
        if(value.name.isNotEmpty()){
            bindingteam.editTextTextPersonName.setText(value.name)
            bindingteam.uploadTeamImage.setImageBitmap(value.imageBitmap)
            bindingteam.editTextTextPersonPosition.setText(value.position)
            bindingteam.editTextTextPersonLinkedin.setText(value.linkedin)
            bindingteam.editTextTextPersoninstagram.setText(value.instagram)
            bindingteam.editTextTextPersongithub.setText(value.github)
        }

        //Visibility if panel data is given.
        if(pos == true){
            bindingteam.editTextTextPersonPosition.visibility = View.VISIBLE
            bindingteam.editTextTextPersonPositionil.visibility = View.VISIBLE
        }

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){
            val res = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
            if(res!=null){
                imageChanged =true
                value.imageBitmap = res
                bindingteam.uploadTeamImage.setImageBitmap(res)
            }
        }
        bindingteam.uploadTeamImageButton.setOnClickListener {
            //Choose Image
            getContent.launch("image/*")
        }


        bindingteam.submitTeam.setOnClickListener {
            //Submit CHanges
            val st = "Confirm Changes?"
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sure ?")
                .setMessage(st)
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    // Respond to negative button press
                    dialog.cancel()
                }
                .setPositiveButton("Ok") { d, which ->
                    // Respond to positive button press
                    try {
                        if(bindingteam.editTextTextPersonName.text.isNullOrBlank()){
                            Toast.makeText(requireContext(), "Name is Required.",Toast.LENGTH_SHORT).show()
                        }else{
                            // get values
                            val editValues = Teams(
                                name = bindingteam.editTextTextPersonName.text.toString(),
                                position = bindingteam.editTextTextPersonPosition.text.toString(),
                                github = bindingteam.editTextTextPersongithub.text.toString(),
                                instagram = bindingteam.editTextTextPersoninstagram.text.toString(),
                                linkedin = bindingteam.editTextTextPersonLinkedin.text.toString(),
                                image = value.image
                            )
                            editValues.imageBitmap = value.imageBitmap
                            model.viewModelScope.launch {
                                val res = editValues.let { it1 -> model.editMember(it1, imageChanged = imageChanged) }
                                try {
                                    if(res.failed !=null){
                                        Toast.makeText(requireContext(), res.failed, Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(requireContext(),"Upload Successfully.",Toast.LENGTH_SHORT).show()
                                    }
                                }catch (e:Exception){
                                    Log.w("Error: ",e.message.toString())
                                }
                            }

                            d.dismiss()
                        }

                    }catch (e:Exception){
                        Log.w("Error",e.message.toString())
                    }
                }.show()
        }
        bindingteam.cancelTeam.setOnClickListener {
            //Cancel Changes
            dialog?.cancel()
        }
    }
}