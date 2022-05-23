package com.example.sankalan.ui.scoreboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.sankalan.data.Score
import com.example.sankalan.databinding.FragmentScoreboardBinding
import com.example.sankalan.model.MainViewModel


class ScoreboardFragment : Fragment() {

    private lateinit var binding: FragmentScoreboardBinding
    private val scoreModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentScoreboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scoreModel.liveResult.observe(viewLifecycleOwner) { it ->
            if(it!=null){
                binding.noResult.visibility = View.GONE
                binding.containerResult.visibility = View.VISIBLE
            }

            //For Spinner
            val eventNames = arrayListOf<String>()
            for (e in it.distinctBy { it.eventName }) {
                eventNames.add(e.eventName)
            }
            val spinnerArrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, eventNames)
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinner.adapter = spinnerArrayAdapter

            //Load Results
            it.find { sco ->
                sco.eventName == binding.spinner.selectedItem
            }?.let { it1 -> loadResult(it1) }
        }//End Observer


    }

    private fun loadResult(score: Score) {

        binding.apply {
            firstResult.text = score.first
            secondResult.text = score.second
            thirdResult.text = score.third
        }
    }


}