package com.example.medicationreminder

import androidx.compose.material3.MaterialTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.medicationreminder.ui.theme.MedicationReminderTheme
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import java.util.*
import android.content.BroadcastReceiver
import android.media.RingtoneManager
import android.media.Ringtone
import android.os.Vibrator

fun setAlarm(context: Context, hour: Int, minute: Int) {
    // Create an Intent to trigger the AlarmReceiver
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    // Get the AlarmManager service
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Set the time for the alarm
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }

    // Set the alarm to start at the specified time
    if (calendar.timeInMillis < System.currentTimeMillis()) {
        // If the time is before now, add a day to the alarm
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    // Set the alarm
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        AlarmManager.INTERVAL_DAY,
        pendingIntent
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge mode
        setContent {
            MedicationReminderTheme {
                // All composable calls must be inside a Composable context
                NavigationSetup() // Start navigation setup
            }
        }
    }
}

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Create an alarm notification sound
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone: Ringtone = RingtoneManager.getRingtone(context, notification)
        ringtone.play()

        // Optionally, you can also add vibration
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(500) // Vibrate for 500 milliseconds

        // Show a notification to the user if needed (Optional)
        // You can create a notification here to inform the user to take medication
    }
}


@Composable
fun NavigationSetup() {
    val navController = rememberNavController() // Create a NavController

    // Set up the NavHost with composable destinations
    NavHost(navController = navController, startDestination = "byTime") {
        composable("main") { MainScreen(navController) } // Main screen
        composable("second") { SecondScreen() } // Second screen
        composable("option2") { Option2(navController) } // Main screen
        composable("byTime") { ThirdScreen(navController) }
        composable("moremed") { AddMoreMed(navController) } // Add more medications
        //composable("petshop") {PetShop(navController)}
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
        Spacer(modifier = Modifier.height(200.dp)) // Space above the button
        Button(
            onClick = {
                navController.navigate("second")
            },
            modifier = Modifier
                .width(300.dp) // Set width for a rectangular shape
                .height(80.dp) // Set height for a rectangular shape
                .graphicsLayer(alpha = if (isVisible.value) 1f else 0f)
        ) {
            Text(text = "Supabutton") // Button label
        }
        Spacer(modifier = Modifier.height(150.dp)) // Optional space below the button
    }
}

@Composable
fun SecondScreen() {
    var userInput by remember { mutableStateOf("") } // State to hold the text input
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
@Composable
fun ThirdScreen(navController: NavController) {
    // State to hold the list of time entries
    val timeEntries = remember { mutableStateListOf(TimeEntry("", "", "")) }
    val isVisible = remember { mutableStateOf(false) }
    // Background image
    Image(
        painter = painterResource(id = R.drawable.screen_three_background),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    // Main Column to hold time entries and buttons
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom // Align items to the bottom
    ) {
        // Display the time entries
        Column(
            modifier = Modifier
                .weight(1F) // Allow this column to take the remaining space
                .verticalScroll(rememberScrollState()) // Make the column scrollable
                .padding(bottom = 100.dp) // Space for the Next button
        ) {
            Spacer(modifier = Modifier.height(250.dp))
            // Display all time entries
            timeEntries.forEach { entry ->
                TimeInputRow(entry)
            }
            // Centered Add More button
            Button(
                onClick = {
                    // Add a new time entry
                    timeEntries.add(TimeEntry("", "", ""))
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally) // Align the button in the center
                    .width(300.dp)
                    .height(100.dp)
                    .padding(top = 16.dp) // Optional padding above the button
            ) {
                Text(text = "Add More") // Button label
            }
        }

        // Next button at the bottom
        Button(
            onClick = {
                navController.navigate("moremed")
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally) // Align the button in the center
                .width(330.dp)
                .height(120.dp)
                .padding(bottom = 40.dp)
                .graphicsLayer(alpha = if (isVisible.value) 1f else 0f)
        ) {
            Text(text = "Next") // Button label
        }
    }


}

// Data class to hold time entry
data class TimeEntry(var hour: String, var minute: String, var amPm: String)

// Composable to render a row for time input
@Composable
fun TimeInputRow(entry: TimeEntry) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center, // Align items horizontally in the center
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hour input
        TextField(
            value = entry.hour,
            onValueChange = { entry.hour = it.filter { char -> char.isDigit() } },
            label = { Text("Hour") }, // Label for the TextField
            modifier = Modifier
                .padding(vertical = 10.dp)
                .padding(2.dp)
                .width(100.dp)
        )

        // Minute input
        TextField(
            value = entry.minute,
            onValueChange = { entry.minute = it.filter { char -> char.isDigit() } },
            label = { Text("Minute") },
            modifier = Modifier
                .padding(vertical = 10.dp)
                .padding(2.dp)
                .width(100.dp) // Adjust width as needed
        )

        // AM/PM input
        TextField(
            value = entry.amPm,
            onValueChange = { entry.amPm = it },
            label = { Text("AM/PM") },
            modifier = Modifier
                .padding(vertical = 10.dp)
                .padding(8.dp)
                .width(100.dp) // Adjust width as needed
        )
    }
}



@Composable
fun Option2(navController: NavController){
    val isVisible = remember { mutableStateOf(false) }
    Image(
        painter = painterResource(id = R.drawable.hoursdose),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
    MedicationInputForm()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom, // Align to bottom
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp)) // Space above the button
        Button(
            onClick = {
                navController.navigate("moremed")
            },
            modifier = Modifier
                .width(300.dp) // Set width for a rectangular shape
                .height(80.dp) // Set height for a rectangular shape
                .graphicsLayer(alpha = if (isVisible.value) 1f else 0f)
        ) {
            Text(text = "Supabutton") // Button label
        }
        Spacer(modifier = Modifier.height(40.dp)) // Optional space below the button
    }
}

@Composable
fun MedicationInputForm() {
    var dosesPerDay by remember { mutableStateOf("") } // State for doses per day
    var numberOfDoses by remember { mutableStateOf("") } // State for number of doses
    var hour by remember { mutableStateOf("") } // State for hour input
    var minute by remember { mutableStateOf("") } // State for minute input
    var amPm by remember { mutableStateOf("") } // State for AM/PM input
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // "How many doses per day?" input
        TextField(
            value = dosesPerDay,
            onValueChange = { dosesPerDay = it },
            label = { Text("Number of doses") },
            modifier = Modifier
                .padding(vertical = 135.dp)
                .fillMaxWidth(0.95f)
        )

        // "Number of doses" input
        TextField(
            value = numberOfDoses,
            onValueChange = { numberOfDoses = it },
            label = { Text("Hours") },
            modifier = Modifier
                .padding(vertical = 30.dp)
                .fillMaxWidth(0.95f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center, // Align items horizontally in the center
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hour input
            TextField(
                value = hour,
                onValueChange = { hour = it },
                label = { Text("Hour") },
                modifier = Modifier
                    .padding(vertical = 140.dp)
                    .padding(15.dp)
                    .width(100.dp) // Adjust width as needed
            )

            // Minute input
            TextField(
                value = minute,
                onValueChange = { minute = it },
                label = { Text("Minute") },
                modifier = Modifier
                    .padding(vertical = 140.dp)
                    .padding(2.dp)
                    .width(120.dp) // Adjust width as needed
            )

            // AM/PM input
            TextField(
                value = amPm,
                onValueChange = { amPm = it },
                label = { Text("AM/PM") },
                modifier = Modifier
                    .padding(vertical = 140.dp)
                    .padding(8.dp)
                    .width(140.dp) // Adjust width as needed
            )
        }
    }
}

@Composable
fun AddMoreMed(navController: NavController) {
    val isVisible = remember { mutableStateOf(false) }
    Image(
        painter = painterResource(id = R.drawable.newmed),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top, // Align to bottom
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
        Spacer(modifier = Modifier.height(213.dp)) // Space above the buttons

        // First button
        Button(
            onClick = {
                navController.navigate("second")
            },
            modifier = Modifier
                .width(400.dp)
                .height(60.dp)
                .graphicsLayer(alpha = if (isVisible.value) 1f else 0f)
        ) {
            Text(text = "Supabutton") // Button label
        }

        Spacer(modifier = Modifier.height(32.dp)) // Space between buttons

        // Second button
        Button(
            onClick = {
                navController.navigate("second")
            },
            modifier = Modifier
                .width(400.dp)
                .height(60.dp)
                .graphicsLayer(alpha = if (isVisible.value) 1f else 0f)
        ) {
            Text(text = "Second Button") // Button label
        }
    }
}
data class PetShopItem(val name: String, val cost: Int)

@Composable
fun PetShop(navController: NavController) {
    val items = listOf(
        PetShopItem("Dog Toy", 10),
        PetShopItem("Cat Toy", 15),
        PetShopItem("Fish Food", 5),
        PetShopItem("Bird Cage", 25)
    )

    var points by remember { mutableStateOf(50) } // Initialize user points
    var purchasedItems by remember { mutableStateOf(listOf<PetShopItem>()) } // Track purchased items

    Image(
        painter = painterResource(id = R.drawable.petshop),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display user points
        Text(text = "Your Points: $points", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // List of items
        LazyColumn {
            items(items) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${item.name} - ${item.cost} Points")
                    Spacer(modifier = Modifier.weight(1f)) // Space between text and button
                    Button(
                        onClick = {
                            if (points >= item.cost) {
                                points -= item.cost // Deduct points
                                purchasedItems = purchasedItems + item // Add item to purchased list
                            } else {
                                // Handle insufficient points
                            }
                        },
                        enabled = points >= item.cost // Disable button if not enough points
                    ) {
                        Text(text = "Buy")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display purchased items
        if (purchasedItems.isNotEmpty()) {
            Text(text = "Purchased Items:", style = MaterialTheme.typography.titleMedium)
            purchasedItems.forEach { item ->
                Text(text = item.name)
            }
        }
    }
}