package com.example.karttasovellus

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

// ViewModel-luokka, joka käsittelee sijaintitietoja ja siihen liittyviä tiloja
class LocationViewModel : ViewModel() {

    // Käyttäjän sijainti, käytetään Compose-sovelluksessa tilan hallintaan
    var userLocation = mutableStateOf<LatLng?>(null)

    // Bool-muuttuja, joka kertoo, onko kartta keskitetty käyttäjän sijaintiin
    var isMapCentered = mutableStateOf(false)

    // Bool-muuttuja, joka ilmaisee, pitäisikö sijaintitieto hakea
    var shouldFetchLocation = mutableStateOf(false)

    // Metodi käyttäjän sijainnin nollaamiseen
    fun resetUserLocation() {
        userLocation.value = null
    }
}