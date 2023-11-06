package com.example.karttasovellus

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.location.Priority

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var userLocation by mutableStateOf<LatLng?>(null)

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                // Käyttöoikeus myönnettiin, hae käyttäjän sijainti
                fetchLocation()
            }
            else -> {
                // Käyttöoikeutta ei myönnetty
                Toast.makeText(
                    this,
                    "Sijaintioikeudet tarvitaan kartan näyttämiseen",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            // Käyttöoikeus on jo myönnetty, hae sijainti
            fetchLocation()
        }

        setContent {
            val cameraPositionState = rememberCameraPositionState()
            MapViewContainer(cameraPositionState)
        }
    }

    private fun fetchLocation() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    val userLocation = locationResult.locations.first()
                    // Update the state holder with the new user location
                    this@MainActivity.userLocation = LatLng(userLocation.latitude, userLocation.longitude)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Käyttöoikeus puuttuu, joten ei tehdä mitään
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    @Composable
    fun MapViewContainer(cameraPositionState: CameraPositionState) {
        val mapView = rememberMapViewWithLifecycle()
        val userLocation by rememberUpdatedState(newValue = userLocation)

        AndroidView({ mapView }) { mapView ->
            mapView.getMapAsync { googleMap ->
                // Check if userLocation is not null
                userLocation?.let { location ->
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 15f)
                    googleMap.moveCamera(cameraUpdate)

                    // Add a marker to the user's location
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title("Olet tässä")
                    )
                }
            }
        }
    }


    @Composable
    fun rememberMapViewWithLifecycle(): MapView {
        val context = LocalContext.current
        val mapView = remember {
            MapView(context).apply {
                // Do initial setup here if needed
            }
        }

        val lifecycle = LocalLifecycleOwner.current.lifecycle
        DisposableEffect(lifecycle) {
            val lifecycleObserver = getMapLifecycleObserver(mapView)
            lifecycle.addObserver(lifecycleObserver)
            onDispose {
                lifecycle.removeObserver(lifecycleObserver)
            }
        }

        return mapView
    }

    private fun getMapLifecycleObserver(mapView: MapView) = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
            Lifecycle.Event.ON_START -> mapView.onStart()
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            Lifecycle.Event.ON_STOP -> mapView.onStop()
            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
            else -> throw IllegalStateException()
        }
    }
}
