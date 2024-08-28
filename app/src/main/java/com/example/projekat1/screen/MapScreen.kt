package com.example.projekat1.screen

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.projekat1.R
import com.example.projekat1.location.LocationService
import com.example.projekat1.models.Adventure
import com.example.projekat1.repositories.Resource
import com.example.projekat1.screen.components.bitmapDescriptorFromVector2
import com.example.projekat1.viewModel.AdventureViewModel
import com.example.projekat1.viewModel.UserAuthViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: UserAuthViewModel,
    adventureViewModel: AdventureViewModel,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    myLocation: MutableState<LatLng?> = remember { mutableStateOf(null) }
) {


    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val adventureCollection = adventureViewModel.adventures.collectAsState()
    val adventuresList = remember { mutableListOf<Adventure>() }
    val selectedAdventure = remember { mutableStateOf<Adventure?>(null) }

    val currentUserId by remember { mutableStateOf(viewModel.getCurrentUserId()) }
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val isTrackingServiceEnabled = sharedPreferences.getBoolean("tracking_location", true)
    val lastLatitude = sharedPreferences.getString("last_latitude", null)?.toDoubleOrNull()
    val lastLongitude = sharedPreferences.getString("last_longitude", null)?.toDoubleOrNull()

    adventureCollection.value.let {
        when (it) {
            is Resource.Success -> {
                adventuresList.clear()
                adventuresList.addAll(it.result)
            }

            is Resource.Loading -> {
                Log.d("MapScreen", "Loading adventures...")
            }

            is Resource.Failure -> {
                Log.e("MapScreen", "Failed to load adventures:")
            }

            null -> {
                Log.d("MapScreen", "No adventure available")
            }
        }
    }

    Log.d("MapScreen", "MapScreen Composable Started")

    if (!isTrackingServiceEnabled && lastLatitude != null && lastLongitude != null) {
        val lastLocation = LatLng(lastLatitude, lastLongitude)
        // Use lastLocation as the position of the map
        cameraPositionState.position = CameraPosition.fromLatLngZoom(lastLocation, 17f)
    }

    // Check location permissions and start LocationService
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.d("MapScreen", "Permissions not granted, requesting permissions")
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )
    } else {
        // Start LocationService
        Log.d("MapScreen", "Permissions granted, starting LocationService")

        Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            context.startForegroundService(this)
        }
    }

    // Register BroadcastReceiver to receive location updates
    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                if (intent?.action == LocationService.ACTION_LOCATION_UPDATE) {
                    val latitude =
                        intent.getDoubleExtra(LocationService.EXTRA_LOCATION_LATITUDE, 0.0)
                    val longitude =
                        intent.getDoubleExtra(LocationService.EXTRA_LOCATION_LONGITUDE, 0.0)
                    myLocation.value = LatLng(latitude, longitude)
                }
            }
        }
    }

    // Register the receiver
    DisposableEffect(context) {
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(receiver, IntentFilter(LocationService.ACTION_LOCATION_UPDATE))
        onDispose {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        }
    }

    LaunchedEffect(myLocation.value) {//dodala launched effect
        myLocation.value?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 17f)
//            Log.d(
//                "MapScreen",
//                "CameraPosition updated to: Latitude: ${location.latitude}, Longitude: ${location.longitude}"
//            )

        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            modifier = Modifier.fillMaxSize()
        ) {
            myLocation.value?.let { location ->
                Log.d(
                    "LocationUpdate",
                    "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                )
                Marker(
                    state = MarkerState(position = location),
                    title = "You are here",
                    snippet = "Current location of the user",
                    onClick = {

                        //showDialog.value = true
                        true
                    }
                )
            }

            adventuresList.forEach { adventure ->
                val adventureLocation =
                    LatLng(adventure.location.latitude, adventure.location.longitude)
                val user = adventure.userId
                val markerIcon = if (adventure.userId == currentUserId) {
                    bitmapDescriptorFromVector2(context, R.drawable.markeravantura)
                } else {
                    Log.d("TUDJI", user)
                    currentUserId?.let { Log.d("moj", it) }
                    bitmapDescriptorFromVector2(context, R.drawable.tudjimarker)
                }
                Marker(
                    state = MarkerState(position = adventureLocation),
                    title = adventure.title,
                    snippet = "By ${adventure.type}",
                    icon = markerIcon,
                    onClick = {
                        selectedAdventure.value = adventure
                        // Navigate to the book details screen
                        Log.d("MapScreen", "Clicked on adventure: ${adventure.title}")
                        true
                    }
                )
            }

        }

        // Header with Sign Out Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xD0E5D6B3)) // Set the header background color
                .padding(horizontal = 8.dp, vertical = 4.dp) // Reduced padding
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AdventureTrails",
                    style = MaterialTheme.typography.h6.copy(fontSize = 18.sp) // Smaller font size
                )

                Row {
                    // Settings Button
                    Button(
                        onClick = {
                            navController.navigate("settingsScreen")
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6D4C41)), // Brown color
                        modifier = Modifier.padding(end = 4.dp) // Reduced space between buttons
                    ) {
                        Text("Settings", color = Color.White, fontSize = 14.sp) // Smaller text size
                    }

                    // Log Out Button
                    Button(
                        onClick = {
                            viewModel.logOut()
                            navController.navigate("loginScreen") {
                                popUpTo("mapScreen") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6D4C41)) // Brown color
                    ) {
                        Text("Log Out", color = Color.White, fontSize = 14.sp) // Smaller text size
                    }
                }
            }
        }



        FloatingActionButton(
            onClick = { showDialog.value = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 60.dp, start = 16.dp),
            backgroundColor = Color(0xC2D4C4B0),
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Adventure")
        }

        // Transparent green dialog that appears when the button is clicked

        if (showDialog.value) {
            AddAdventureDialog(
                location = myLocation.value,
                onDismiss = { showDialog.value = false },
                onSave = { title, description, type, level, images ->
                    adventureViewModel.saveAdventure(
                        location = myLocation,
                        title = title,
                        description = description,
                        type = type,
                        level = level,
                        adventureImages = images
                    )
                    showDialog.value = false // Dismiss the dialog after saving
                }
            )
        }
        selectedAdventure.value?.let { adventure ->
            // Display a details window at the bottom of the screen
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(
                        Color(0xFFB9FBC0),
                        RoundedCornerShape(12.dp)
                    ) // Light green background
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    adventure.adventureImages.firstOrNull()?.let { imageUri ->
                        // Show the first image from adventureImages
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .background(Color.Gray)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(2f)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = adventure.title,
                            style = MaterialTheme.typography.body1.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32) // Match header color
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Type: ${adventure.type}",
                            style = MaterialTheme.typography.body2.copy(
                                color = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Level: ${adventure.level}",
                            style = MaterialTheme.typography.body2.copy(
                                color = Color.Gray
                            )
                        )
                    }
                }
            }
        }
        BottomAppBar(
            backgroundColor = Color(0xFFE5D6B3), // Color same as header
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(0.dp)
        ) {
            // Map icon
            IconButton(
                onClick = {
                    navController.navigate("mapScreen") {
                        popUpTo("mapScreen") { inclusive = true }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = "Map",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Map",
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            // Rank icon
            IconButton(
                onClick = {
                    navController.navigate("rankScreen") {
                        popUpTo("mapScreen") { inclusive = true }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.FormatListNumbered, // Placeholder for rank icon
                        contentDescription = "Rank",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rank",
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            // Profile icon
            IconButton(
                onClick = {
                    navController.navigate("profileScreen") {
                        popUpTo("mapScreen") { inclusive = true }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }

}

