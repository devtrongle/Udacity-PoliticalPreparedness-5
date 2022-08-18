package com.example.android.politicalpreparedness.election

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoterInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val tag = "VoterInfoViewModel"
    private val database: ElectionDatabase = ElectionDatabase.getInstance(application)

    private var election: Election? = null

    //TODO: Add var and methods to support loading URLs
    private val _url = MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url

    private val _isFollowed = MutableLiveData<Boolean>(false)
    val isFollowed: LiveData<Boolean>
        get() = _isFollowed

    //TODO: Add var and methods to populate voter info
    //TODO: Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    fun setVotingLocationFinderUrl(){
        _voterInfo.value?.let { vote ->
            vote.state?.let { state->
                if (state.isNotEmpty()) {
                    state.first().electionAdministrationBody.votingLocationFinderUrl?.let {
                        _url.value = it
                    }
                }
            }
        }
    }

    fun setBallotInfoUrl(){
        _voterInfo.value?.let { vote ->
            vote.state?.let { state->
                if (state.isNotEmpty()) {
                    state.first().electionAdministrationBody.ballotInfoUrl?.let {
                        _url.value = it
                    }
                }
            }
        }
    }

    suspend fun getVoteInfo(electionId: Int) {
        withContext(Dispatchers.IO) {
            try {
                val election = getElection(electionId)
                election?.let {
                    val address = it.division.country + "/" + it.division.state
                    val rs = CivicsApi.retrofitService.getVoterInfo(address, electionId)
                    _voterInfo.postValue(rs)
                }
            } catch (ex: Exception) {
                Log.e(tag, ex.toString())
            }
        }
    }

    //TODO: Add var and methods to save and remove elections to local database
    fun followElection() {
        viewModelScope.launch {
            _isFollowed.value?.let { follow ->
                election?.apply {
                    saveElection(this, follow)
                }
            }
        }
    }

    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status
    private suspend fun saveElection(election: Election, follow: Boolean) {
        withContext(Dispatchers.IO) {
            election.saved = if (follow) 0 else 1
            database.electionDao.update(election)
            this@VoterInfoViewModel.election = election
            _isFollowed.postValue(election.saved != 0)
        }
    }


    private fun getElection(electionId: Int): Election? {
        val election = database.electionDao.getElection(electionId)
        election?.apply {
            this@VoterInfoViewModel.election = this
            _isFollowed.postValue(saved != 0)
            return this
        }
        return null
    }
    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}