package com.example.sankalan.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sankalan.model.MainViewModel
import com.example.sankalan.adapter.GalleryListAdapter
import com.example.sankalan.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val galleryViewModel: MainViewModel by activityViewModels()
    private lateinit var adapter:GalleryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recycle_gallery = binding.recyclerViewGallery
        recycle_gallery.layoutManager = GridLayoutManager(requireContext(),4)
        recycle_gallery.setHasFixedSize(true)

        galleryViewModel.images_gallery.observe(viewLifecycleOwner, Observer {
            adapter = GalleryListAdapter(it)
            recycle_gallery.adapter = adapter
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}