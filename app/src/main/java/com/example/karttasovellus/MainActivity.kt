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

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel by viewModels<LocationViewModel>()
    private lateinit var firestoreManager: FirestoreManager

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        firestoreManager = FirestoreManager()
        checkLocationPermission()

        setContent {
            val navController = rememberNavController()
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
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            fetchLocation()
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                viewModel.userLocation.value = LatLng(it.latitude, it.longitude)
            }
        }
    }
}
