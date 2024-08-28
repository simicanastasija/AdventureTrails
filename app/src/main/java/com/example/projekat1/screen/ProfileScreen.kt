package com.example.projekat1.screen

import androidx.compose.foundation.background
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
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.projekat1.models.Adventure
import com.example.projekat1.models.User
import com.example.projekat1.repositories.Resource
import com.example.projekat1.viewModel.AdventureViewModel
import com.example.projekat1.viewModel.UserAuthViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun ProfileScreen(
    viewModel: AdventureViewModel,
    userAuthViewModel:  UserAuthViewModel,
    navController: NavController
) {
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
                        onClick = {
                            navController.navigate("settingsScreen") {
                                popUpTo("profileScreen") { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                backgroundColor = Color(0xFFE5D6B3), // Header background color
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
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Prikaz kartica za avanture korisnika
                            Text(
                                text = "Moje avanture",
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(bottom = 8.dp)
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
                text = adventure.description,
                style = MaterialTheme.typography.body1
            )
        }
    }
}




