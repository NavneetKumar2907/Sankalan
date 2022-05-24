package com.sankalan.dialogfragments

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
import com.sankalan.R
import com.sankalan.data.Teams
import com.sankalan.databinding.EditTeamPanelBinding
import com.sankalan.model.AdminViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * Class For Editing Team Dialog Fragment.
 */
class EditTeam(var value: Teams, var pos: Boolean = false) : DialogFragment() {

    lateinit var bindingteam: EditTeamPanelBinding
    val model by activityViewModels<AdminViewModel>()
    var imageChanged: Boolean = false //Image Changer CHeck

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
        if (value.name.isNotEmpty()) {
            bindingteam.editTextTextPersonName.setText(value.name)
            bindingteam.uploadTeamImage.setImageBitmap(value.imageBitmap)
            bindingteam.editTextTextPersonPosition.setText(value.position)
            bindingteam.editTextTextPersonLinkedin.setText(value.linkedin)
            bindingteam.editTextTextPersoninstagram.setText(value.instagram)
            bindingteam.editTextTextPersongithub.setText(value.github)
            bindingteam.editTextTextPersonPhone.setText(value.phone)
        }

        //Visibility if panel data is given.
        if (pos == true) {
            bindingteam.editTextTextPersonPosition.visibility = View.VISIBLE
            bindingteam.editTextTextPersonPositionil.visibility = View.VISIBLE
            bindingteam.editTextTextPersonPhone.visibility = View.VISIBLE
            bindingteam.editTextTextPersonPhoneil.visibility = View.VISIBLE

            // Gone Views
            bindingteam.editTextTextPersoninstagram.visibility = View.GONE
            bindingteam.editTextTextPersongithub.visibility = View.GONE
            bindingteam.editTextTextPersoninstagramil.visibility = View.GONE
            bindingteam.editTextTextPersongithubil.visibility = View.GONE

        }
        //Fetching Image.
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            val res = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
            if (res != null) {
                imageChanged = true
                value.imageBitmap = res
                bindingteam.uploadTeamImage.setImageBitmap(res)
            }
        }

        //Upload Listener
        bindingteam.uploadTeamImageButton.setOnClickListener {
            //Choose Image
            getContent.launch("image/*")
        }
        //Submit Listener
        bindingteam.submitTeam.setOnClickListener {
            //Submit CHanges
            val st = "Confirm Changes?"
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sure ?")
                .setMessage(st)
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                    // Respond to negative button press
                    dialog.cancel()
                }//End Negative
                .setPositiveButton("Ok") { d, _ ->
                    // Respond to positive button press
                    try {
                        if (bindingteam.editTextTextPersonName.text.isNullOrBlank()) {
                            Toast.makeText(
                                requireContext(),
                                "Name is Required.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // get values
                            val editValues = Teams(
                                name = bindingteam.editTextTextPersonName.text.toString(),
                                position = bindingteam.editTextTextPersonPosition.text.toString(),
                                github = bindingteam.editTextTextPersongithub.text.toString(),
                                instagram = bindingteam.editTextTextPersoninstagram.text.toString(),
                                linkedin = bindingteam.editTextTextPersonLinkedin.text.toString(),
                                image = value.image,
                                phone = bindingteam.editTextTextPersonPhone.text.toString()
                            )//Creating Team Object

                            editValues.imageBitmap =
                                value.imageBitmap //Setting Up Image Bitmap for upload

                            model.viewModelScope.launch {
                                //Coroutine for uploading data

                                val res = editValues.let { it1 ->
                                    model.editMember(
                                        it1,
                                        imageChanged = imageChanged
                                    )
                                }
                                try {
                                    if (res.failed != null) {
                                        Toast.makeText(
                                            requireContext(),
                                            res.failed,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Upload Successfully.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Log.w("Error: ", e.message.toString())
                                }// End Try catch
                            }//End Coroutine
                            d.dismiss()
                        }//end if else

                    } catch (e: Exception) {
                        Log.w("Error", e.message.toString())
                    }//End Try
                }.show()//End Dialog
        }
        bindingteam.cancelTeam.setOnClickListener {
            //Cancel Changes
            dialog?.cancel()
        }
    }
}