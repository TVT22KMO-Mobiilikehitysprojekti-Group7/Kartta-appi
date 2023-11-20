package com.example.karttasovellus

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController, viewModel: LocationViewModel) {
    // Kun HomeScreen ladataan, resetoidaan käyttäjän sijainti
    LaunchedEffect(Unit) {
        viewModel.resetUserLocation()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter // Aseta sisältö alareunaan keskelle
    ) {
        Image(
            painter = painterResource(id = R.drawable.map_background),
            contentDescription = "Taustakuva",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("map/60.192059/24.945831") },
                modifier = Modifier.padding(bottom = 8.dp) // Väli napin ja toisen napin välillä
            ) {
                Text("Avaa kartta")
            }
            Button(
                onClick = { navController.navigate("savedLocations") }
            ) {
                Text("Katso tallennettuja sijainteja")
            }

        }
    }
}
