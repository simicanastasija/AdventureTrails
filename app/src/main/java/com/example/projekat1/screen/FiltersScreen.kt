package com.example.projekat1.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.maps.android.compose.Circle

@Composable
fun FiltersScreen(
    navController: NavController,
    onApplyFilters: (Map<String, String>) -> Unit
) {
    var selectedType by remember { mutableStateOf<String?>(null) }
    var selectedLevel by remember { mutableStateOf<String?>(null) }
    var selectedCommentRange by remember { mutableStateOf<String?>(null) }
    var selectedVisitorCount by remember { mutableStateOf(0) }
    var selectedRadius by remember { mutableStateOf(1) }

    val adventureTypes = listOf("Planinarenje", "Kampovanje", "Biciklizam", "Trcanje", "Drugo")
    val adventureLevels = listOf("Easy", "Moderate", "Hard")
    val commentRanges = listOf("0-20", "21-40", "41-60", "60-100")

    val selectedColor = Color(0xFF6A9F5A) // Custom color for selected radio button
    val unselectedColor = Color(0xFFB9D3B1) // Custom color for unselected radio button

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Adventure Trails Filters",
                        style = MaterialTheme.typography.h6
                    )
                },
                backgroundColor = Color(0xFFE5D6B3),
                contentColor = Color.Black,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Filter by adventure type
                    item {
                        Text("Tip avanture", style = MaterialTheme.typography.h6)
                    }
                    items(adventureTypes) { type ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = selectedColor,
                                    unselectedColor = unselectedColor
                                )
                            )
                            Text(text = type, modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    // Filter by adventure level
                    item {
                        Text("Nivo avanture", style = MaterialTheme.typography.h6)
                    }
                    items(adventureLevels) { level ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedLevel == level,
                                onClick = { selectedLevel = level },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = selectedColor,
                                    unselectedColor = unselectedColor
                                )
                            )
                            Text(text = level, modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    // Filter by comment count
                    item {
                        Text("Broj komentara", style = MaterialTheme.typography.h6)
                    }
                    items(commentRanges) { range ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedCommentRange == range,
                                onClick = { selectedCommentRange = range },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = selectedColor,
                                    unselectedColor = unselectedColor
                                )
                            )
                            Text(text = range, modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    // Filter by visitor count
                    item {
                        Text("Minimalan broj posetilaca: $selectedVisitorCount", style = MaterialTheme.typography.h6)
                    }
                    item {
                        Slider(
                            value = selectedVisitorCount.toFloat(),
                            onValueChange = { newValue ->
                                selectedVisitorCount = newValue.toInt()
                            },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFD0BFA2),
                                activeTrackColor = Color(0xFFD0BFA2),
                                inactiveTrackColor = Color(0xFFD0BFA2)
                            )
                        )
                    }

                    // Filter by radius
                    item {
                        Text("Radius (km): $selectedRadius", style = MaterialTheme.typography.h6)
                    }
                    item {
                        Slider(
                            value = selectedRadius.toFloat(),
                            onValueChange = { newValue ->
                                selectedRadius = newValue.toInt()
                            },
                            valueRange = 1f..1000f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFD0BFA2),
                                activeTrackColor = Color(0xFFD0BFA2),
                                inactiveTrackColor = Color(0xFFD0BFA2)
                            )
                        )
                    }
                }

                Button(
                    onClick = {
                        val filters = mutableMapOf<String, String>().apply {
                            selectedType?.let { put("type", it) }
                            selectedLevel?.let { put("level", it) }
                            selectedCommentRange?.let { put("comments", it) }
                            put("visitors", selectedVisitorCount.toString())
                            put("radius", selectedRadius.toString())
                        }

                        onApplyFilters(filters)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFBFAE94),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Text("Primeni filtere")
                }
            }
        }
    )
}


