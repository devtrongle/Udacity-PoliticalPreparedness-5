package com.example.android.politicalpreparedness.representative

import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class RepresentativeViewModel(): ViewModel() {
    private val tag = "RepresentativeViewModel"

    //TODO: Establish live data for representatives and address
    private val _representatives = MutableLiveData<List<Representative>>()
    private val _address = MutableLiveData(Address())
    private val _showToast = MutableLiveData<String>()

    val representatives: LiveData<List<Representative>>
        get() = _representatives

    val address: LiveData<Address>
        get() = _address

    val showToast: LiveData<String>
        get() = _showToast

    fun setAddress(address: Address){
        _address.value = address
    }

    //TODO: Create function to fetch representatives from API from a provided address
    suspend fun getRepresentatives() {
        withContext(Dispatchers.IO) {
            try {
                _address.value?.let { it ->
                    val (offices , officials) = CivicsApi.retrofitService.getRepresentatives(it.toFormattedString())
                    val result = offices.flatMap { office -> office.getRepresentatives(officials) }
                    _representatives.postValue(result)
                    return@withContext
                }

                _showToast.postValue("Address is Null")
            } catch (exception : Exception) {
                _showToast.postValue(exception.toString())
                Log.e(tag, exception.toString())
            }
        }
    }
    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

}
