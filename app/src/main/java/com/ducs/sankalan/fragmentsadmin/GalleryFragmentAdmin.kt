package com.ducs.sankalan.fragmentsadmin

import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.ducs.sankalan.R
import com.ducs.sankalan.adapter.AdminGalleryAdapter
import com.ducs.sankalan.databinding.FragmentGalleryAdminBinding
import com.ducs.sankalan.model.AdminViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Gallery Fragment Class.
 */
class GalleryFragmentAdmin : Fragment() {

    lateinit var galleryBinding: FragmentGalleryAdminBinding //Binding
    val galleryModel by activityViewModels<AdminViewModel>() //View Model
    var imageBitmapList = ArrayList<Bitmap>() //Image List

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        galleryBinding = FragmentGalleryAdminBinding.inflate(layoutInflater)
        return galleryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Getting Image For Upload
        val getContent = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            for (uri in it) {
                //If Multiple Images are selected
                imageBitmapList.add(
                    MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        uri
                    )
                )
            }
            //Uploading Image
            galleryModel.uploadImages(imageBitmapList)
        }

        //Add Image Launcger
        galleryBinding.addImage.setOnClickListener {
            // add Image
            getContent.launch("image/*")
        }
        galleryBinding.deleteAllImage.setOnClickListener {
            //ALert Dialog Before Deleting All.
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sure Delete All Images from gallery?")
                .setMessage("It will delete All the images from gallery and its will not recover.")
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                    // Respond to negative button press
                    dialog.cancel()
                }
                .setPositiveButton("Ok") { _, _ ->
                    // Respond to positive button press
                    galleryModel.deleteAll()//Deletes All Image
                }
                .show()//End Dialog
        }//End Listener

        //Recycle View SetUp
        galleryBinding.uploadedeImages.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(this)
        }

        //Image Value Observer
        galleryModel.imagesLive.observe(viewLifecycleOwner) {
            galleryBinding.uploadedeImages.adapter = AdminGalleryAdapter(it)
        }
    }


}