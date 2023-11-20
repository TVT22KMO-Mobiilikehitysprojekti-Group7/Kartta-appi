package com.example.karttasovellus

import com.google.firebase.Timestamp

// Data-luokka, joka kuvaa sijaintitietoja
data class LocationData(
    // Oletusarvoina annetaan nollat, jos arvoja ei anneta
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val note: String = "",

    // Oletusarvona käytetään nykyhetkeä, kun luokkaa luodaan
    val timestamp: Timestamp = Timestamp.now()
)
