package com.main.sankalan.ui.sponsers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.main.sankalan.model.MainViewModel
import com.main.sankalan.databinding.FragmentSponsersBinding


class SponsersFragment : Fragment() {

    lateinit var bindingSponser:FragmentSponsersBinding
    val sponserViewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingSponser = FragmentSponsersBinding.inflate(inflater)
        return bindingSponser.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingSponser.sponsersList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
        sponserViewModel.liveSponser.observe(viewLifecycleOwner, Observer {
            bindingSponser.sponsersList.adapter = SponserAdapter(it)
        })

    }


}