package com.example.sankalan.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.adapter.EventListAdapter
import com.example.sankalan.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recycleEventList:RecyclerView
    private lateinit var adapter:EventListAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recycleEventList = binding.recyclerViewHome
        recycleEventList.setHasFixedSize(true)
        recycleEventList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)

        val viewmodel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        viewmodel.getEvent().observe(viewLifecycleOwner, Observer {
            adapter = EventListAdapter(it)
            recycleEventList.adapter = adapter
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}