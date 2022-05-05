package com.example.sankalan.fragmentsadmin

import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.system.Os.accept
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sankalan.R
import com.example.sankalan.adapter.AdminGalleryAdapter
import com.example.sankalan.databinding.FragmentGalleryAdminBinding
import com.example.sankalan.model.AdminViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class GalleryFragmentAdmin : Fragment() {

    lateinit var galleryBinding:FragmentGalleryAdminBinding
    val galleryModel by activityViewModels<AdminViewModel>()
    var imageBitmapList= ArrayList<Bitmap>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        galleryBinding = FragmentGalleryAdminBinding.inflate(layoutInflater)
        return galleryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val getContent = registerForActivityResult(ActivityResultContracts.GetMultipleContents()){
            for (uri in it) {
                imageBitmapList.add(MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri))
            }
            galleryModel.uploadImages(imageBitmapList)
        }

        galleryBinding.addImage.setOnClickListener {
            // add Image
            getContent.launch("image/*")
        }
        galleryBinding.deleteAllImage.setOnClickListener {
            //ALert Dialog
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sure Delete All Images from gallery?")
                .setMessage("It will delete All the images from gallery and its will not recover.")
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    // Respond to negative button press
                    dialog.cancel()
                }
                .setPositiveButton("Ok") { dialog, which ->
                    // Respond to positive button press
                    galleryModel.deleteAll()
                }
                .show()
        }

        galleryBinding.uploadedeImages.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
        galleryModel.imagesLive.observe(viewLifecycleOwner, Observer {
            galleryBinding.uploadedeImages.adapter = AdminGalleryAdapter(it)
        })
    }


}