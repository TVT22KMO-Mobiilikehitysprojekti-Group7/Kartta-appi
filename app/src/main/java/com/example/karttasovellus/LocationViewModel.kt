package com.example.karttasovellus

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class LocationViewModel : ViewModel() {
    var userLocation = mutableStateOf<LatLng?>(null)

}