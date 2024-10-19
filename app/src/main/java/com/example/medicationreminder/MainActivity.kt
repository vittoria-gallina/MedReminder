package com.example.medicationreminder
import androidx.compose.material3.Button
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.navigation.compose.NavHost
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medicationreminder.ui.theme.MedicationReminderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedicationReminderTheme {

                NavigationSetup()

            }
        }
    }
}


@Composable
fun NavigationSetup() {
    val navController = rememberNavController() // Create a NavController

    NavHost(navController, startDestination = "main") { // Define the navigation graph
        composable("main") { MainScreen(navController) } // Main screen
        composable("second") { SecondScreen() } // Second screen
    }
}




@Composable
fun MainScreen(navController: NavController){
    val isVisible = remember { mutableStateOf(false) }
    Image(
        painter = painterResource(id = R.drawable.background_background),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom, // Align to bottom
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp)) // Space above the button
        Button(
            onClick = {
                navController.navigate("second")
                Log.d("NAVIGATION", "Navigating to second screen")
            },
            modifier = Modifier
                .width(300.dp) // Set width for a rectangular shape
                .height(100.dp) // Set height for a rectangular shape
                .graphicsLayer(alpha = if (isVisible.value) 1f else 0f)
        ) {
            Text(text = "Supabutton") // Button label
        }
        Spacer(modifier = Modifier.height(130.dp)) // Optional space below the button
    }
}

@Composable
fun SecondScreen() {
    var userInput by remember { mutableStateOf("") } // State to hold the text input
    Image(
        painter = painterResource(id = R.drawable.background2),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TextField for user input
        TextField(
            value = userInput,
            onValueChange = { userInput = it }, // Update the state on value change
            label = { Text("Enter something") }, // Label for the TextField
            modifier = Modifier
                .padding(bottom = 20.dp)
                .width(300.dp) // Adjust width as needed
        )

        // Welcome message text
        Text(text = "Welcome to the Second Screen!", modifier = Modifier.padding(16.dp)) // Content of the second screen
    }
}