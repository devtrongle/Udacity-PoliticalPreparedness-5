package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import kotlinx.coroutines.launch

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding
    private val args: VoterInfoFragmentArgs by navArgs()
    private val viewModel: VoterInfoViewModel by lazy {
        ViewModelProvider(this, VoterInfoViewModelFactory(requireActivity().application))[VoterInfoViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)
        //TODO: Add ViewModel values and create ViewModel
        //TODO: Add binding values
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */
        val electionId = args.argElectionId

        lifecycleScope.launch {
            viewModel.getVoteInfo(electionId)
        }

        //TODO: Handle loading of URLs
        viewModel.url.observe(viewLifecycleOwner){
            loadUrl(it)
        }

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
        return binding.root
    }

    //TODO: Create method to load URL intents
    private fun loadUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}