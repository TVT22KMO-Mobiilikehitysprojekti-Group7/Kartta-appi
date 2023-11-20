package com.example.karttasovellus

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.android.gms.maps.model.LatLng
import android.util.Log

class FirestoreManager {

    // Alustetaan Firestore-tietokantayhteys Firebase-kirjaston kautta
    private val db = Firebase.firestore

    // Tallentaa sijainnin ja liittyvän huomautuksen Firestore-tietokantaan
    fun saveLocationWithNote(location: LatLng, note: String) {
        // Luodaan HashMap sijaintitietojen tallentamista varten
        val locationData = hashMapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "note" to note,
            "timestamp" to com.google.firebase.Timestamp.now()
        )

        // Lisätään sijaintitiedot "locations" -kokoelmaan Firestore-tietokantaan
        db.collection("locations").add(locationData)
            .addOnSuccessListener { documentReference ->
                // Onnistuneesti tallennettu, tulostetaan dokumentin ID lokissa
                Log.d("Firestore", "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                // Tallennuksessa tapahtui virhe, tulostetaan virhe lokissa
                Log.w("Firestore", "Error adding document", e)
            }
    }

    // Hakee tallennetut sijainnit Firestore-tietokannasta ja välittää ne callbackin avulla
    fun getSavedLocations(callback: (List<LocationData>) -> Unit) {
        // Haetaan kaikki dokumentit "locations" -kokoelmasta
        db.collection("locations")
            .get()
            .addOnSuccessListener { documents ->
                // Muunnetaan dokumentit LocationData-olioiksi ja välitetään ne callbackin kautta
                val locationsList = documents.mapNotNull { doc ->
                    doc.toObject(LocationData::class.java)
                }
                callback(locationsList)
            }
            .addOnFailureListener { exception ->
                // Hakemisessa tapahtui virhe, tulostetaan virhe lokissa
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }
}