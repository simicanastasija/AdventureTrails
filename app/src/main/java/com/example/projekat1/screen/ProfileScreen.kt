package com.example.projekat1.screen

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.projekat1.location.LocationService
import com.example.projekat1.models.Adventure
import com.example.projekat1.models.User
import com.example.projekat1.repositories.Resource
import com.example.projekat1.viewModel.AdventureViewModel
import com.example.projekat1.viewModel.UserAuthViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    viewModel: AdventureViewModel,
    userAuthViewModel: UserAuthViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val isTrackingServiceEnabled = sharedPreferences.getBoolean("tracking_location", false)

    val checked = remember {
        mutableStateOf(isTrackingServiceEnabled)
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    CustomLogoutDialog(
        showLogoutDialog = showLogoutDialog,
        onDismiss = { showLogoutDialog = false },
        onConfirm = {
            userAuthViewModel.logOut()
            navController.navigate("loginScreen") {
                popUpTo("profileScreen") { inclusive = true }
            }
            showLogoutDialog = false
        }
    )


    fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    val isServiceRunning = isServiceRunning(LocationService::class.java)
    val currentUserFlow by userAuthViewModel.currentUserFlow.collectAsState()
    val userAdventures by viewModel.userAdventures.collectAsState()

    LaunchedEffect(currentUserFlow) {
        if (currentUserFlow is Resource.Success) {
            val user = (currentUserFlow as Resource.Success<User>).result
            user.id?.let { id ->
                viewModel.getUserAdventures(id)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AdventureTrails",
                        style = MaterialTheme.typography.h6
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showLogoutDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Log Out"
                        )
                    }
                },
                backgroundColor = Color(0xFFE5D6B3),
                contentColor = Color.Black
            )
        },
        bottomBar = {
            BottomAppBar(
                backgroundColor = Color(0xFFE5D6B3), // Footer background color
                contentColor = Color.Black
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Map,
                            contentDescription = "Map",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Map", style = MaterialTheme.typography.body2)
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.FormatListNumbered,
                            contentDescription = "Rank",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Rank", style = MaterialTheme.typography.body2)
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Profile", style = MaterialTheme.typography.body2)
                    }
                }
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Adjust for header and footer
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(horizontal = 16.dp)
                ) {
                    when (currentUserFlow) {
                        is Resource.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                        is Resource.Success -> {
                            val user = (currentUserFlow as Resource.Success<User>).result

                            // Create a Row to align image and data horizontally
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Display profile image
                                user.profileImg?.let { imageUri ->
                                    AsyncImage(
                                        model = imageUri,
                                        contentDescription = "Profile Image",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(CircleShape)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Display user data
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = "${user.fullName}",
                                        style = MaterialTheme.typography.h6
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Email: ${user.email}",
                                        style = MaterialTheme.typography.body1
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Phone: ${user.phoneNumber}",
                                        style = MaterialTheme.typography.body1
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Points: ${user.totalPoints}",
                                        style = MaterialTheme.typography.body1
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            // Adventure nearby toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(5.dp))
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Adventure nearby!",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        color = Color(0xFF6F4F28)
                                    )
                                )
                                Switch(
                                    checked = checked.value,
                                    onCheckedChange = {
                                        checked.value = it
                                        if (it) {
                                            Intent(context, LocationService::class.java).apply {
                                                action = LocationService.ACTION_FIND_NEARBY
                                                context.startForegroundService(this)
                                            }
                                            with(sharedPreferences.edit()) {
                                                putBoolean("tracking_location", true)
                                                apply()
                                            }
                                        } else {
                                            Intent(context, LocationService::class.java).apply {
                                                action = LocationService.ACTION_STOP
                                                context.stopService(this)
                                                Log.d("ServiceSettings", "Stop action sent")
                                            }
                                            Intent(context, LocationService::class.java).apply {
                                                action = LocationService.ACTION_START
                                                context.startForegroundService(this)
                                                Log.d("ServiceSettings", "Start action sent")
                                            }
                                            with(sharedPreferences.edit()) {
                                                putBoolean("tracking_location", false)
                                                apply()
                                            }
                                        }
                                    },
                                    thumbContent = {
                                        if (checked.value) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        } else {
                                            // Provide a default thumb icon or null
                                            null
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            // Display user adventures
                            Text(
                                text = "My Adventures",
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
                            )

                            when (userAdventures) {
                                is Resource.Loading -> {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                                }
                                is Resource.Success -> {
                                    val adventures = (userAdventures as Resource.Success<List<Adventure>>).result
                                    LazyColumn {
                                        items(adventures) { adventure ->
                                            AdventureCard(adventure)
                                        }
                                    }
                                }
                                is Resource.Failure -> {
                                    Text(
                                        text = "Error loading adventures: ${(userAdventures as Resource.Failure).toString()}",
                                        color = Color.Red,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        }
                        is Resource.Failure -> {
                            Text(
                                text = "Error loading user data: ${(currentUserFlow as Resource.Failure).toString()}",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                        else -> {
                            Text(
                                text = "No user data available",
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun AdventureCard(adventure: Adventure) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = adventure.title,
                style = MaterialTheme.typography.h6
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Visited by: ${adventure.visitedUsers.size} users",
                style = MaterialTheme.typography.body1,
                color= Color.Gray
            )

            Text(
                text = adventure.description,
                style = MaterialTheme.typography.body1
            )


        }
    }
}


@Composable
fun CustomLogoutDialog(
    showLogoutDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showLogoutDialog) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                elevation = 8.dp,
                color = Color.White.copy(alpha = 0.9f), // Adjust transparency here
                modifier = Modifier.width(300.dp) // Set a fixed width for the dialog
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Confirm Logout",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Are you sure you want to log out?",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onConfirm,
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB71C1C)),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Log Out", color = Color.White)
                        }
                        Button(
                            onClick = { onDismiss() },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB0BEC5)),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}






