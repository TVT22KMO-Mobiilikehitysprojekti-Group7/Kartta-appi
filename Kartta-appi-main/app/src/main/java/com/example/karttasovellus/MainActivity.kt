package com.example.karttasovellus

import SavedLocationsScreen
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import androidx.navigation.NavType
import androidx.navigation.navArgument

// Pääaktiviteetti, joka aloittaa sovelluksen ja määrittelee navigaatiot
class MainActivity : ComponentActivity() {

    // Google Play Servicesin sijaintipalvelun käyttö
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Käytetään Jetpack Compose -näkymien yhteydessä
    private val viewModel by viewModels<LocationViewModel>()

    // FirestoreManager-luokka vastaa Firebase-tietokannan käsittelystä
    private lateinit var firestoreManager: FirestoreManager

    // Vakio sijaintioikeuden pyytämiseen
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Alustetaan sijaintipalvelun käyttö
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Alustetaan FirestoreManager
        firestoreManager = FirestoreManager()

        // Tarkistetaan ja pyydetään tarvittaessa sijaintioikeutta
        checkLocationPermission()

        // Asetetaan Compose-näkymä
        setContent {
            val navController = rememberNavController()

            // Määritetään navigaatiot
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(navController, viewModel)
                }
                composable(
                    "map/{latitude}/{longitude}",
                    arguments = listOf(
                        navArgument("latitude") { type = NavType.StringType },
                        navArgument("longitude") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull()
                    val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull()
                    MapScreen(viewModel, firestoreManager, latitude, longitude)
                }
                composable("savedLocations") {
                    SavedLocationsScreen(firestoreManager, navController)
                }
            }
        }
    }

    // Tarkistetaan ja pyydetään sijaintioikeutta tarvittaessa
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Sijaintioikeus on jo myönnetty, joten haetaan sijainti
            fetchLocation()
        }
    }

    // Haetaan laitteen viimeisin sijainti
    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }

        // Haetaan viimeisin sijainti ja päivitetään ViewModelin tila
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                viewModel.userLocation.value = LatLng(it.latitude, it.longitude)
            }
        }
    }
}