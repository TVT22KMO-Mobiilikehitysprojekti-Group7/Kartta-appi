package com.example.karttasovellus

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.android.gms.maps.model.LatLng
import android.util.Log

class FirestoreManager {

    private val db = Firebase.firestore

    fun saveLocationWithNote(location: LatLng, note: String) {
        val locationData = hashMapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "note" to note,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        db.collection("locations").add(locationData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }

    fun getSavedLocations(callback: (List<LocationData>) -> Unit) {
        db.collection("locations")
            .get()
            .addOnSuccessListener { documents ->
                val locationsList = documents.mapNotNull { doc ->
                    doc.toObject(LocationData::class.java)
                }
                callback(locationsList)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }
}
