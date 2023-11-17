package com.example.karttasovellus

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel by viewModels<LocationViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(navController)
                }
                composable("map") {
                    MapScreen(viewModel)
                }
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Pyydä käyttöoikeuksia
        } else {
            // Käyttöoikeus on jo myönnetty, hae sijainti
            fetchLocation()
        }
    }

    @Composable
    fun MapScreen(viewModel: LocationViewModel) {
        val defaultLocation = LatLng(65.0121, 25.4651)
        val isMapCentered by viewModel.isMapCentered

        LaunchedEffect(viewModel.shouldFetchLocation.value) {
            if (viewModel.shouldFetchLocation.value) {
                fetchLocation()
                viewModel.shouldFetchLocation.value = false
            }
        }

        Box {
            MapViewContainer(isMapCentered, defaultLocation)

            Button(
                onClick = {
                    viewModel.isMapCentered.value = true
                    viewModel.shouldFetchLocation.value = true // Päivitä sijainti
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text("Keskitä kartta sijaintiini")
            }
        }
    }


    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                viewModel.userLocation.value = LatLng(it.latitude, it.longitude)
            }
        }
    }


    @Composable
    fun MapViewContainer(centerMap: Boolean, defaultLocation: LatLng) {
        val mapView = rememberMapViewWithLifecycle()
        val userLocationState by rememberUpdatedState(newValue = viewModel.userLocation.value)

        AndroidView({ mapView }) { mapViewInstance ->
            mapViewInstance.getMapAsync { googleMap ->
                // Ensimmäisellä renderöinnillä, siirrä kamera oletussijaintiin
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))

                if (centerMap) {
                    val userLocation = userLocationState // Tallenna arvo paikalliseen muuttujaan
                    if (userLocation != null) {
                        // Keskitä kartta käyttäjän sijaintiin ja lisää merkki, jos centerMap on true ja userLocation ei ole null
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13f))
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(userLocation)
                                .title("Olet tässä")
                        )
                    }
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
