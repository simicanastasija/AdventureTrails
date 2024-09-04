package com.example.projekat1.screen

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.projekat1.screen.appComponent.LevelSelection
import com.example.projekat1.screen.appComponent.UploadAdventureImages
import com.example.projekat1.screen.components.CustomLabel
import com.google.android.gms.maps.model.LatLng

@Composable
fun AddAdventureDialog(
    location: LatLng?,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, type: String, level: String, images: List<Uri>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("Easy") }
    val selectedMoreImages = remember { mutableStateOf<List<Uri>>(emptyList()) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFE5D6B3), // Updated to be solid, not transparent
            modifier = Modifier
                .width(320.dp) // Slightly wider to give buttons more space
                .height(600.dp) // Increased height for better button placement
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Title
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp), // Reduced spacing
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add Adventure",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black // Changed title to black
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp)) // Adjusted spacer

                // Title input
                CustomLabel(label = "Title")
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Enter adventure title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Description input (not resizing the entire layout for large text)
                CustomLabel(label = "Description")
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Enter adventure description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp), // Fixed height for the description field
                    maxLines = 3 // Restrict the number of lines to prevent layout shifting
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Type input
                CustomLabel(label = "Type")
                TextField(
                    value = type,
                    onValueChange = { type = it },
                    placeholder = { Text("Enter adventure type") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Level selection
                CustomLabel(label = "Level")
                LevelSelection(
                    selectedLevel = selectedLevel,
                    onLevelSelected = { newLevel -> selectedLevel = newLevel }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Image upload
                CustomLabel(label = "Add Images")
                UploadAdventureImages(
                    selectedImages = selectedMoreImages,
                    onImagesSelected = { images ->
                        selectedMoreImages.value = images
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Buttons Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp), // Extra space above buttons
                    horizontalArrangement = Arrangement.End // Align buttons to the right
                ) {
                    // Cancel button
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(48.dp), // Ensure buttons have sufficient height
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5E3C)) // Better contrast
                    ) {
                        Text("Cancel", color = Color.White)
                    }

                    // Save button
                    Button(
                        onClick = {
                            if (location != null) {
                                onSave(title, description, type, selectedLevel, selectedMoreImages.value)
                            }
                            onDismiss()
                        },
                        modifier = Modifier.height(48.dp), // Ensure buttons have sufficient height
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A9F5A)) // Better contrast
                    ) {
                        Text("Save", color = Color.White)
                    }
                }
            }
        }
    }
}