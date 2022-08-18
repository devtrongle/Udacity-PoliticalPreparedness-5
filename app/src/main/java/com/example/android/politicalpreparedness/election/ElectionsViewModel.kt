package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(application: Application): AndroidViewModel(application){
    private val tag = "ElectionsViewModel"
    private val database = ElectionDatabase.getInstance(application)

    //TODO: Create live data val for upcoming elections
    val upcomingElections = database.electionDao.getElections()

    //TODO: Create live data val for saved elections
    val saveElection = database.electionDao.getElectionsSaved()

    private val _navigateVoterInfo = MutableLiveData<Election?>()
    val navigateVoterInfo : LiveData<Election?>
    get() = _navigateVoterInfo

    init {
        viewModelScope.launch {
            getElectionsFromAPI()
        }
    }

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    private suspend fun getElectionsFromAPI() {
        withContext(Dispatchers.IO) {
            try {
                val result = CivicsApi.retrofitService.getElections()
                database.electionDao.inserts(result.elections)
            } catch (ex: Exception) {
                Log.e(tag, ex.toString())
            }
        }
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info
    fun onUpcomingElectionClick(election: Election){
        _navigateVoterInfo.value = election
    }

    fun onSavedElectionClick(election: Election){
        _navigateVoterInfo.value = election
    }

    fun resetNavigate() {
        _navigateVoterInfo.value = null
    }
}