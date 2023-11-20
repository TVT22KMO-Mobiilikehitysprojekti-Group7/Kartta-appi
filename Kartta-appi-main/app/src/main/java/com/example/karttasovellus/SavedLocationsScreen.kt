import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.karttasovellus.FirestoreManager
import com.example.karttasovellus.LocationData

@Composable
fun SavedLocationsScreen(firestoreManager: FirestoreManager, navController: NavHostController) {
    val savedLocations = remember { mutableStateOf<List<LocationData>>(listOf()) }

    LaunchedEffect(Unit) {
        firestoreManager.getSavedLocations { locations ->
            savedLocations.value = locations
        }
    }

    LazyColumn {
        items(savedLocations.value) { location ->
            Card(
                modifier = Modifier
                    .clickable {
                        navController.navigate("map/${location.latitude}/${location.longitude}")
                    }
                    .padding(8.dp)
            ) {
                Text("Sijainti: ${location.latitude}, ${location.longitude}, Huomio: ${location.note}", modifier = Modifier.padding(8.dp))
            }
        }
    }
}
