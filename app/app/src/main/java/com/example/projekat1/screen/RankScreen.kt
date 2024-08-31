package com.example.projekat1.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.projekat1.repositories.Resource
import com.example.projekat1.viewModel.UserAuthViewModel
import java.lang.reflect.Modifier

/*@Composable
fun RankScreen(
    navController: NavController,
    userAuthViewModel: UserAuthViewModel // Assume UserViewModel handles fetching and managing users' points
) {
    // Fetch users sorted by points
    val usersState by userAuthViewModel.usersState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Rankings",
                        style = MaterialTheme.typography.h6,
                        color = Color.Black // Text color
                    )
                },
                backgroundColor = Color(0xFFE5D6B3), // Header background color
                contentColor = Color.Black // Content (e.g., icons) color
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (usersState) {
                is Resource.Loading -> {
                    Text("Loading rankings...", style = MaterialTheme.typography.h6)
                }
                is Resource.Success -> {
                    val users = (usersState as Resource.Success).result

                    // Display ranked users in a LazyColumn
                    LazyColumn {
                        itemsIndexed(users.sortedByDescending { it.points }) { index, user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}.",
                                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.width(32.dp)
                                )

                                Text(
                                    text = user.username,
                                    style = MaterialTheme.typography.body1,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "${user.points} points",
                                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Divider() // Divider between users
                        }
                    }
                }
                is Resource.Failure -> {
                    Text("Error loading rankings.", style = MaterialTheme.typography.h6)
                }
                else -> {
                    Text("No users found.", style = MaterialTheme.typography.h6)
                }
            }
        }
    }
}*/
