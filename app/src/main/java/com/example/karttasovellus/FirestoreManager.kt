package com.example.karttasovellus

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.android.gms.maps.model.LatLng
import android.util.Log

class FirestoreManager {

    private val db = Firebase.firestore

    fun saveLocation(location: LatLng) {
        val locationData = hashMapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
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

    fun fetchLocations() {
        db.collection("locations")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("Firestore", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }
}
