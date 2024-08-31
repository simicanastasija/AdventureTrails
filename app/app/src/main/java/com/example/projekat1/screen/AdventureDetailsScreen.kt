package com.example.projekat1.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.projekat1.models.Comment
import com.example.projekat1.models.User
import com.example.projekat1.repositories.Resource
import com.example.projekat1.viewModel.AdventureViewModel
import com.example.projekat1.viewModel.UserAuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AdventureDetailsScreen(
    navController: NavController,
    adventureId: String,
    adventureViewModel: AdventureViewModel,
    viewModel: UserAuthViewModel // Pass UserAuthViewModel
) {
    // Fetch the selected adventure by ID from the viewModel
    LaunchedEffect(adventureId) {
        adventureViewModel.getAdventureById(adventureId)
    }

    // Fetch current user data
    LaunchedEffect(Unit) {
        viewModel.getUserData()
    }

    val adventureState by adventureViewModel.adventureState.collectAsState()
    val currentUserState by viewModel.currentUserFlow.collectAsState()

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    var commentText by remember { mutableStateOf(TextFieldValue("")) }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Add a Comment", style = MaterialTheme.typography.h6, color = Color.Black)

                Spacer(modifier = Modifier.height(8.dp))

                BasicTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(8.dp)
                        .background(Color(0xFFE5D6B3), shape = MaterialTheme.shapes.small),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val currentUser = when (val userResource = currentUserState) {
                                is Resource.Success -> userResource.result
                                else -> null
                            }

                            val userId = currentUser?.id ?: "unknownUserId"
                            val userName = currentUser?.fullName ?: "Unknown User"

                            val newComment = Comment(
                                userId = userId,
                                userName = userName,
                                timestamp = System.currentTimeMillis(),
                                text = commentText.text
                            )

                            adventureViewModel.addComment(userId, adventureId, newComment)
                            commentText = TextFieldValue("") // Clear text after adding comment
                            sheetState.hide() // Close the modal sheet
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Submit")
                }
            }
        },
        sheetBackgroundColor = Color.White,
        sheetContentColor = Color.Black,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "AdventureTrails",
                            style = MaterialTheme.typography.h6,
                            color = Color.Black // Text color
                        )
                    },
                    backgroundColor = Color(0xFFE5D6B3), // Header background color
                    contentColor = Color.Black // Content (e.g., icons) color
                )
            },
            bottomBar = {
                BottomAppBar(
                    backgroundColor = Color(0xFFE5D6B3), // Color same as header
                    contentColor = Color.Black,
                    modifier = Modifier
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
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // Adventure details
                when (adventureState) {
                    is Resource.Loading -> {
                        // Loading state
                        Text("Loading adventure details...", style = MaterialTheme.typography.h6)
                    }
                    is Resource.Success -> {
                        val adventure = (adventureState as Resource.Success).result
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Adventure title
                            Text(
                                text = adventure.title,
                                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFE5D6B3)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Adventure description
                            Text(
                                text = "Description: ${adventure.description}",
                                style = MaterialTheme.typography.body1
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Adventure type and level
                            Text(
                                text = "Type: ${adventure.type}",
                                style = MaterialTheme.typography.body2
                            )
                            Text(
                                text = "Level: ${adventure.level}",
                                style = MaterialTheme.typography.body2
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Location details
                            Text(
                                text = "Location: Latitude ${adventure.location.latitude}, Longitude ${adventure.location.longitude}",
                                style = MaterialTheme.typography.body2
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Adventure images
                            val scrollState = rememberScrollState()
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(scrollState)
                                    .padding(vertical = 8.dp)
                            ) {
                                adventure.adventureImages.forEach { imageUrl ->
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Adventure Image",
                                        modifier = Modifier
                                            .size(120.dp) // Smaller image size
                                            .padding(4.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        sheetState.show() // Show the comment bottom sheet
                                    }
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Add Comment")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Comments Section
                            LazyColumn {
                                items(adventure.comments) { comment ->
                                    CommentItem(comment = comment)
                                }
                            }
                        }
                    }
                    is Resource.Failure -> {
                        // Error state
                        Text("Error loading adventure details", style = MaterialTheme.typography.h6)
                    }
                    else -> {
                        // Adventure not found or null state
                        Text("Adventure not found.", style = MaterialTheme.typography.h6)
                    }
                }
            }
        }
    }
}


@Composable
fun CommentItem(comment: Comment) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(comment.userName, fontWeight = FontWeight.Bold)
                Text(
                    text = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(comment.timestamp),
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Comment text
            Text(comment.text, style = MaterialTheme.typography.body1)
        }
    }
}
