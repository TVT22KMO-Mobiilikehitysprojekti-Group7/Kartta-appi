package com.example.karttasovellus

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.karttasovellus.R

@Composable
fun HomeScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.map_background),
            contentDescription = "Taustakuva",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Skaalaa kuva sopivaksi
        )

        Button(onClick = { navController.navigate("map") }) {
            Text("Avaa kartta")
        }
    }
}
