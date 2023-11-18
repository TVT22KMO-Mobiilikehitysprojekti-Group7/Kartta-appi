package com.example.karttasovellus

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
fun MapScreen(viewModel: LocationViewModel, firestoreManager: FirestoreManager) {
    val defaultLocation = LatLng(65.0121, 25.4651)
    val isMapCentered by viewModel.isMapCentered
    val userLocation by viewModel.userLocation

    LaunchedEffect(userLocation) {
        userLocation?.let {
            firestoreManager.saveLocation(it)
        }
    }

    Box {
        MapViewContainer(isMapCentered, defaultLocation, userLocation)

        Button(
            onClick = {
                viewModel.isMapCentered.value = true
                viewModel.shouldFetchLocation.value = true
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("Keskitä kartta sijaintiini")
        }
    }
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
