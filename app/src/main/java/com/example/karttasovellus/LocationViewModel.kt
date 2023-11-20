package com.example.karttasovellus

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class LocationViewModel : ViewModel() {
    var userLocation = mutableStateOf<LatLng?>(null)
    var isMapCentered = mutableStateOf(false)
    var shouldFetchLocation = mutableStateOf(false)

    fun resetUserLocation() {
        userLocation.value = null
    }
}
