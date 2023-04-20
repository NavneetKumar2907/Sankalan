package com.main.sankalan.ui.scoreboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.sankalan.adapter.ScoreAdapter
import com.main.sankalan.data.Score
import com.main.sankalan.databinding.FragmentScoreboardBinding
import com.main.sankalan.model.MainViewModel


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

            binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val score:Score? = it.find { it.eventName==binding.spinner.selectedItem }
                    loadResult(score!!)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Toast.makeText(context, "No Results", Toast.LENGTH_SHORT).show()
                }

            }
        }//End Observer




    }

    private fun loadResult(score: Score) {

        binding.scoreList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = ScoreAdapter(score.result)
        }
    }


}