package com.example.karttasovellus

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {
    Button(onClick = { navController.navigate("map") }) {
        Text("Avaa kartta")
    }
}