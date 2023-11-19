package com.example.karttasovellus

import com.google.firebase.Timestamp

data class LocationData(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val note: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
