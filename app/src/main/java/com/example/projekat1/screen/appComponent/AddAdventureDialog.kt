package com.example.projekat1.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.projekat1.screen.appComponent.LevelDropdownMenu
import com.example.projekat1.screen.appComponent.LevelSelection
import com.example.projekat1.screen.appComponent.UploadAdventureImages
import com.example.projekat1.screen.components.CustomInput
import com.example.projekat1.screen.components.CustomLabel
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAdventureDialog(
    location: LatLng?,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, type: String, level: String, images: List<Uri>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("Easy") }
    val selectedMoreImages = remember { mutableStateOf<List<Uri>>(emptyList()) }
    //var isDropdownExpanded by remember { mutableStateOf(false) }
    //var selectedLevel by remember { mutableStateOf("Select adventure level") }
    var selectedLevel by remember { mutableStateOf("Easy") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xE6F0F8E7).copy(alpha = 0.8f),
            modifier = Modifier
                .width(300.dp) // Set a smaller width
                .height(600.dp) // Set a smaller height
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp) // Reduced padding
            ) {
                // Center the title text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp), // Adjust spacing as needed
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add Adventure",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                CustomLabel(label = "Title")
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Enter adventure title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                CustomLabel(label = "Description")
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Enter adventure description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                CustomLabel(label = "Type")
                TextField(
                    value = type,
                    onValueChange = { type = it },
                    placeholder = { Text("Enter adventure type") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                CustomLabel(label = "Level")

                LevelSelection(
                    selectedLevel = selectedLevel,
                    onLevelSelected = { selectedLevel = it }
                )

                /*Column(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = selectedLevel,
                        onValueChange = { /* No-op */ },
                        readOnly = true,
                        placeholder = { Text("Select adventure level") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDropdownExpanded = true }
                    )

                    LevelDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        selectedLevel = selectedLevel,
                        onLevelSelected = { newLevel ->
                            selectedLevel = newLevel
                            isDropdownExpanded = false // Close the dropdown after selection
                        }
                    )
                }*/


                Spacer(modifier = Modifier.height(6.dp))

                CustomLabel(label = "Add Images")
                UploadAdventureImages(
                    selectedImages = selectedMoreImages,
                    onImagesSelected = { images ->
                        selectedMoreImages.value = images
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End // Align buttons to the right
                ) {
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(end = 8.dp) // Space between Cancel and Save
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (location != null) {
                                onSave(title, description, type, level, selectedMoreImages.value)
                            }
                            onDismiss()
                        }
                    ) {
                        Text("Save")
                    }
                }

            }
        }
    }
}




