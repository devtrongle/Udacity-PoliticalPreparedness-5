package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment : Fragment() {

    private lateinit var binding: FragmentElectionBinding
    private lateinit var upcomingElectionAdapter: ElectionListAdapter
    private lateinit var savedElectionAdapter: ElectionListAdapter

    //TODO: Declare ViewModel
    private val viewModel: ElectionsViewModel by lazy {
        ViewModelProvider(
            this,
            ElectionsViewModelFactory(requireActivity().application)
        )[ElectionsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)
        //TODO: Add ViewModel values and create ViewModel
        //TODO: Add binding values
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        upcomingElectionAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.onUpcomingElectionClick(election)
        })
        savedElectionAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.onSavedElectionClick(election)
        })

        //TODO: Initiate recycler adapters
        binding.recyclerViewUpcomingElection.adapter = upcomingElectionAdapter
        //TODO: Populate recycler adapters
        binding.recyclerViewSavedElection.adapter = savedElectionAdapter

        initObserver()
        return binding.root
    }

    //TODO: Link elections to voter info
    private fun navigateVoterInfo(election: Election) {
        findNavController().navigate(
            ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment
                (election.id, election.division)
        )
    }

    //TODO: Refresh adapters when fragment loads
    private fun initObserver() {
        viewModel.upcomingElections.observe(viewLifecycleOwner) {
            upcomingElectionAdapter.submitList(it)
        }

        viewModel.saveElection.observe(viewLifecycleOwner) {
            savedElectionAdapter.submitList(it)
        }

        viewModel.navigateVoterInfo.observe(viewLifecycleOwner) {
            it?.apply {
                findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment
                        (id, division)
                )
                viewModel.resetNavigate()
            }
        }
    }
}