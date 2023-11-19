package com.example.karttasovellus

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
fun MapScreen(
    viewModel: LocationViewModel,
    firestoreManager: FirestoreManager,
    latitude: Double? = null,
    longitude: Double? = null
) {
    // Määritellään oletussijainti ennen käyttöä
    val defaultLocation = LatLng(65.0121, 25.4651)

    // Määritellään kohdesijainti perustuen annettuihin latitude ja longitude -arvoihin
    val targetLocation = if (latitude != null && longitude != null) {
        LatLng(latitude, longitude)
    } else {
        defaultLocation
    }

    val isMapCentered by viewModel.isMapCentered
    val userLocation by viewModel.userLocation
    var showSaveDialog by remember { mutableStateOf(false) }

    Box {
        MapViewContainer(isMapCentered, targetLocation, userLocation)

        Button(
            onClick = {
                viewModel.isMapCentered.value = true
                viewModel.shouldFetchLocation.value = true
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("Näytä sijaintini")
        }
        Button(
            onClick = { showSaveDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("Tallenna sijaintini")
        }

        if (showSaveDialog) {
            SaveLocationDialog(
                onDismissRequest = { showSaveDialog = false },
                onSave = { note ->
                    userLocation?.let {
                        firestoreManager.saveLocationWithNote(it, note)
                    }
                    showSaveDialog = false
                }
            )
        }
    }
}

@Composable
fun SaveLocationDialog(onDismissRequest: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Tallenna sijainti") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Huomio") }
            )
        },
        confirmButton = {
            Button(onClick = { onSave(text) }) {
                Text("Tallenna")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Peruuta")
            }
        }
    )
}



@Composable
fun MapViewContainer(centerMap: Boolean, defaultLocation: LatLng, userLocation: LatLng?) {
    val mapView = rememberMapViewWithLifecycle()

    AndroidView({ mapView }) { mapViewInstance ->
        mapViewInstance.getMapAsync { googleMap ->
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))

            if (centerMap && userLocation != null) {
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



@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            // Initial setup if needed
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
