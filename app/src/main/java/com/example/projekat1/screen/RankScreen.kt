package com.example.projekat1.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomAppBar
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projekat1.R
import com.example.projekat1.models.User
import com.example.projekat1.navigation.Routes
import com.example.projekat1.repositories.Resource
import com.example.projekat1.viewModel.UserAuthViewModel

@Composable
fun RankScreen(
    userViewModel: UserAuthViewModel,
    navController: NavController
) {
    userViewModel.getAllUsersData()
    val allUsersCollection = userViewModel.allUsers.collectAsState()
    val users = remember { mutableListOf<User>() }

    LaunchedEffect(allUsersCollection?.value) {
        when (val it = allUsersCollection?.value) {
            is Resource.Success -> {
                users.clear()
                users.addAll(it.result.sortedByDescending { user -> user.totalPoints })
            }
            is Resource.Failure -> {
            }
            Resource.Loading -> {
            }
            null -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Set background color to white
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 60.dp) // Dajemo prostor za `BottomAppBar`
        ) {
            // Top App Bar with navigation back
            TopAppBar(
                title = {
                    Text(
                        text = "Adventure Trails Rank",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                },
                backgroundColor = Color(0xFFE5D6B3),
                contentColor = Color.Black,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display user ranking cards
            users.forEachIndexed { index, user ->
                UserRankingCard(user = user, rank = index + 1, navController, user.id)
            }
        }

        // BottomAppBar na dnu ekrana
        BottomAppBar(
            backgroundColor = Color(0xFFE5D6B3), // Boja kao heder
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomCenter) // Postavljanje na dno ekrana
        ) {
            // Map icon
            androidx.compose.material.IconButton(
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
                    androidx.compose.material.Icon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = "Map",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    androidx.compose.material.Text(
                        text = "Map",
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            // Rank icon
            androidx.compose.material.IconButton(
                onClick = {
                    navController.navigate("rankScreen") {
                        popUpTo("mapScreen") { inclusive = false }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material.Icon(
                        imageVector = Icons.Filled.FormatListNumbered, // Placeholder for rank icon
                        contentDescription = "Rank",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    androidx.compose.material.Text(
                        text = "Rank",
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            // Profile icon
            androidx.compose.material.IconButton(
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
                    androidx.compose.material.Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    androidx.compose.material.Text(
                        text = "Profile",
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}

@Composable
fun UserRankingCard(
    user: User,
    rank: Int,
    navController: NavController,
    userId: String
) {
    val medalIcon = when (rank) {
        1 -> painterResource(id = R.drawable.gold) // Zlatna medalja
        2 -> painterResource(id = R.drawable.silver) // Srebrna medalja
        3 -> painterResource(id = R.drawable.bronze) // Bronzana medalja
        else -> null // Za mesta posle 3 nema medalje
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE5D6B3)) // Boja kartice kao heder
            .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Prikaz medalje za top 3 mesta
            if (medalIcon != null) {
                Image(
                    painter = medalIcon,
                    contentDescription = "Medal",
                    modifier = Modifier.size(32.dp) // Prilagoditi veličinu ikone
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                // Ako nema medalje, prikazujemo broj
                Text(
                    text = "$rank.",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(32.dp),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Prikaz korisničkog imena i bodova
            Column {
                Text(
                    text = user.fullName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "${user.totalPoints} points",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}


