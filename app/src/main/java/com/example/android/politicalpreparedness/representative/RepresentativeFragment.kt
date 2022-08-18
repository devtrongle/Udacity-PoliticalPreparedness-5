package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import java.util.Locale

class DetailFragment : Fragment() {

    private lateinit var representativeListAdapter: RepresentativeListAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentRepresentativeBinding

    //TODO: Declare ViewModel
    private val viewModel: RepresentativeViewModel by lazy {
        RepresentativeViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        //TODO: Establish bindings
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)

        //TODO: Define and assign Representative adapter
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //TODO: Populate Representative adapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.states))
        binding.state.adapter = adapter

        //TODO: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            lifecycleScope.launch {
                viewModel.getRepresentatives();
            }
        }

        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions()
        }

        binding.root.setOnClickListener {
            hideKeyboard()
        }

        representativeListAdapter = RepresentativeListAdapter()
        binding.representativeRecyclerview.adapter = representativeListAdapter

        initObserve()

        return binding.root
    }

    private fun initObserve(){
        viewModel.representatives.observe(viewLifecycleOwner) {
            representativeListAdapter.submitList(it)
            binding.listPlaceholder.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.address.observe(viewLifecycleOwner) {
            binding.address = it
        }

        viewModel.showToast.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationPermissions() {
        if (isPermissionGranted()) {
            getLocation()
        } else {
            //TODO: Request Location permissions
            registerForActivityResult.launch(ACCESS_FINE_LOCATION)
        }
    }


    private val registerForActivityResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            checkLocationPermissions()
        }
    }

    private fun isPermissionGranted() : Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        return ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        //TODO: Get location from LocationServices
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    location?.let { it ->
                        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
                        geoCodeLocation(it)
                    }
                }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        val result = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
        viewModel.setAddress(result)
        return result
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}