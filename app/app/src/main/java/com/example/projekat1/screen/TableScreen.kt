package com.example.projekat1.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.ButtonDefaults

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.projekat1.models.Adventure
import com.example.projekat1.viewModel.AdventureViewModel
import com.example.projekat1.repositories.Resource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.projekat1.navigation.Routes.adventureDetailsScreen

@SuppressLint("MutableCollectionMutableState")
@Composable
fun TableScreen(adventureViewModel: AdventureViewModel, navController: NavController) {
    val adventures by adventureViewModel.adventures.collectAsState()
    val userFullNames by remember { mutableStateOf(adventureViewModel._userFullNames) }
    val adventureImages by remember { mutableStateOf(adventureViewModel.adventureImages) }

    LaunchedEffect(Unit) {
        adventureViewModel.getAllAdventures()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Adventures on Map") },
                backgroundColor = Color(0xFFE5D6B3),
                navigationIcon = {
                    IconButton(onClick = {
                       // navController.navigateUp() // Vraća korisnika na prethodni ekran
                        navController.navigate("mapScreen") {
                            popUpTo("tableScreen") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                when (adventures) {
                    is Resource.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is Resource.Success -> {
                        val adventureList = (adventures as Resource.Success<List<Adventure>>).result

                        LazyColumn {
                            items(adventureList) { adventure ->
                                AdventureRow(adventure, userFullNames, navController)
                                Divider()
                            }
                        }

                    }
                    is Resource.Failure -> {
                        Text(
                            text = "Error loading adventures: ${(adventures as Resource.Failure).toString()}",
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        Text(
                            text = "No adventures available",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    )
}




@Composable
fun AdventureRow(
    adventure: Adventure,
    userFullNames: Map<String, String>,
    navController: NavController // Add navController to navigate to details
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Show the first image from the list if available
        val imageUri = adventure.adventureImages.firstOrNull()

        imageUri?.let {
            AsyncImage(
                model = imageUri,
                contentDescription = "Adventure Image",
                modifier = Modifier
                    .size(90.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colors.surface)
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Title: ${adventure.title}",
                style = MaterialTheme.typography.body1
            )
            Text(
                text = "Type: ${adventure.type}",
                style = MaterialTheme.typography.body2
            )
            Text(
                text = "Level: ${adventure.level}",
                style = MaterialTheme.typography.body2
            )
            Text(
                text = "User: ${userFullNames[adventure.userId] ?: "Unknown User"}",
                style = MaterialTheme.typography.body2
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Bottom)
        ) {
            Button(
                onClick = {
                    // Navigate to the AdventureDetailsScreen, passing the adventure ID
                    navController.navigate("adventureDetailsScreen/${adventure.id}")
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .height(30.dp)
                    .padding(horizontal = 6.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF8B4513), // Background color
                    contentColor = Color.White // Text color
                )
            ) {
                Text(
                    text = "View Details",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}










