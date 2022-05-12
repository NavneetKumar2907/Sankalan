package com.example.sankalan.ui.gallery

import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.adapter.GalleryListAdapter
import com.example.sankalan.databinding.FragmentGalleryBinding
import com.example.sankalan.model.MainViewModel

class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val galleryViewModel: MainViewModel by activityViewModels()

    val handler = Handler()
    val scroll = 0
    lateinit var runnable:Runnable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val llm: LinearLayoutManager = object : LinearLayoutManager(requireContext() , HORIZONTAL,false) {
            override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
                val scroller: LinearSmoothScroller =
                    object : LinearSmoothScroller(requireContext()) {
                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                            return 2000f
                        }
                    }
                scroller.targetPosition = position
                startSmoothScroll(scroller)
            }
        }
        binding.recyclerViewGallery.apply {
            layoutManager = llm
            setHasFixedSize(true)
        }


        galleryViewModel.images_gallery.observe(viewLifecycleOwner, Observer {
            val adapter = GalleryListAdapter(it)
            binding.recyclerViewGallery.adapter = adapter
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       runnable = object : Runnable {
           var count = 0
           override fun run() {
               if (count == binding.recyclerViewGallery.adapter?.itemCount) count = 0
               if (count < binding.recyclerViewGallery.adapter?.itemCount?:-1) {
                   binding.recyclerViewGallery.smoothScrollToPosition(++count)
                   handler.postDelayed(this, scroll.toLong())
               }
           }
       }
        handler.postDelayed(runnable, scroll.toLong())
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }




}