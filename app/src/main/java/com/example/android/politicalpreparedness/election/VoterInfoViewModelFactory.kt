package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDatabase

//TODO: Create Factory to generate VoterInfoViewModel with provided election datasource
class VoterInfoViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)){
            return VoterInfoViewModel(application) as T
        }
        throw IllegalArgumentException("Unable to construct viewModel")
    }
}