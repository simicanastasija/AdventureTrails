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
import com.example.projekat1.R
import com.example.projekat1.models.Comment
import com.example.projekat1.models.User
import com.example.projekat1.repositories.Resource
import com.example.projekat1.viewModel.AdventureViewModel
import com.example.projekat1.viewModel.UserAuthViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AdventureDetailsScreen(
    navController: NavController,
    adventureId: String,
    adventureViewModel: AdventureViewModel,
    viewModel: UserAuthViewModel
) {
    val userState by viewModel.currentUserFlow.collectAsState()

    val currentUser = (userState as? Resource.Success)?.result
    val userId = currentUser?.id ?: "unknownUserId"

    // Fetch the selected adventure by ID from the viewModel
    LaunchedEffect(adventureId) {
        adventureViewModel.getAdventureById(adventureId, userId)
        adventureViewModel.loadCommentsForAdventure(adventureId)
    }

    // Fetch current user data
    LaunchedEffect(Unit) {
        viewModel.getUserData()
    }

    val adventureState by adventureViewModel.adventureState.collectAsState()
    val commentState = adventureViewModel.specificAdventureComments.collectAsState()

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    var commentText by remember { mutableStateOf(TextFieldValue("")) }

    val isVisited by remember {
        derivedStateOf {
            (adventureState as? Resource.Success)?.result?.visitedUsers?.contains(userId) ?: false
        }
    }

    val isCurrentUserOwner by remember {
        derivedStateOf {
            (adventureState as? Resource.Success)?.result?.userId == userId
        }
    }


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
                            val newComment = Comment(
                                userId = userId,
                                userName = currentUser?.fullName ?: "Unknown User",
                                timestamp = System.currentTimeMillis(),
                                text = commentText.text
                            )

                            adventureViewModel.addComment(userId, adventureId, newComment)
                            commentText = TextFieldValue("") // Clear text after adding comment
                            sheetState.hide() // Close the modal sheet
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE5D6B3)) // Header color
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
                    contentColor = Color.Black, // Content (e.g., icons) color
                    navigationIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.sign),
                            contentDescription = "Icon App",
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .size(24.dp)
                        )
                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    backgroundColor = Color(0xFFE5D6B3), // Color same as header
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
                                imageVector = Icons.Filled.FormatListNumbered,
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
                Box(modifier = Modifier.fillMaxSize()) {
                    // Adventure details
                    when (adventureState) {
                        is Resource.Loading -> {
                            Text("Loading adventure details...", style = MaterialTheme.typography.h6)
                        }
                        is Resource.Success -> {
                            val adventure = (adventureState as Resource.Success).result
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .align(Alignment.Center)
                            ) {
                                // Adventure title
                                Text(
                                    text = adventure.title,
                                    style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold),
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Visited by ${adventure.visitedUsers.size} users",
                                    style = MaterialTheme.typography.body2,
                                    color = Color.Red
                                )

                                if (!isCurrentUserOwner) {
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        Button(
                                            onClick = {
                                                if (!isVisited) {
                                                    adventureViewModel.markAdventureAsVisited(adventureId, userId, adventure.level)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = if (isVisited) Color.Gray else Color(0xFFE5D6B3)
                                            ),
                                            modifier = Modifier
                                                .align(Alignment.BottomStart) // Align button to bottom start
                                        ) {
                                            Text(if (isVisited) "Visited" else "Mark as Visited")
                                        }
                                    }
                                }

                                // Adventure description
                                Text(
                                    text = adventure.description,
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


                                // Add Comment Button
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            sheetState.show() // Show the comment bottom sheet
                                        }
                                    },
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(16.dp),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE5D6B3)) // Header color
                                ) {
                                    Text("Add Comment")
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Comments List
                                LazyColumn {
                                    items(commentState.value) { comment ->
                                        CommentItem(comment = comment)
                                    }
                                }
                            }

                            // Mark as Visited Button
                            Spacer(modifier = Modifier.height(16.dp))


                        }
                        is Resource.Failure -> {
                            Text("Failed to load adventure details", style = MaterialTheme.typography.h6)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun CommentItem(comment: Comment) {
    Card(
        backgroundColor = Color.White,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            // Profile picture (placeholder)
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "User Profile",
                modifier = Modifier.size(40.dp),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 64.dp) // Add padding to reserve space for date
            ) {
                // Commenter's name
                Text(
                    text = comment.userName,
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                )

                // Comment text
                Text(
                    text = comment.text,
                    style = MaterialTheme.typography.body2
                )
            }

            // Timestamp
            Column(
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(start = 8.dp)
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(comment.timestamp)),
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }
        }
    }
}




