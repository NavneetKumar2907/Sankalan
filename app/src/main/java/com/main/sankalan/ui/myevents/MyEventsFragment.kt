package com.main.sankalan.ui.myevents

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.main.sankalan.adapter.MyEventAdapter
import com.main.sankalan.databinding.FragmentMyEventsBinding
import com.main.sankalan.model.MainViewModel

class MyEventsFragment : Fragment() {
    private var _binding: FragmentMyEventsBinding? = null

    private val binding get() = _binding!!
    private lateinit var recyclerMyEventList: RecyclerView
    private lateinit var adapter: MyEventAdapter

    private val MyEventViewmodel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyEventsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerMyEventList = binding.recyclerViewMyEvents
        recyclerMyEventList.setHasFixedSize(true)
        recyclerMyEventList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        MyEventViewmodel.eventWiseMember.observe(viewLifecycleOwner, Observer {
            Log.w("EVENT MEMEMBER", "${it}")
            if (it != null) {
                adapter = MyEventAdapter(it)
                recyclerMyEventList.adapter = adapter
            } else {
                Toast.makeText(context, "Null List", Toast.LENGTH_SHORT).show()
            }

        })
        return root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}