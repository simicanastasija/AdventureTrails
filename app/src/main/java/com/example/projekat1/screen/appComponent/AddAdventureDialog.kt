package com.example.projekat1.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.projekat1.screen.appComponent.LevelDropdownMenu
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
    var level by remember { mutableStateOf("Easy") }
    val selectedMoreImages = remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0x8000FF00) // Zeleni providni sloj
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Add Adventure", style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp))

                Spacer(modifier = Modifier.height(16.dp))

                CustomLabel(label = "Title")
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Enter adventure title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomLabel(label = "Description")
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Enter adventure description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomLabel(label = "Type")
                TextField(
                    value = type,
                    onValueChange = { type = it },
                    placeholder = { Text("Enter adventure type") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomLabel(label = "Level")
                Box(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = level,
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
                        selectedLevel = level,
                        onLevelSelected = { selectedLevel ->
                            level = selectedLevel
                            isDropdownExpanded = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                CustomLabel(label = "Add Images")
                UploadAdventureImages(
                    selectedImages = selectedMoreImages,
                    onImagesSelected = { images ->
                        selectedMoreImages.value = images
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { onDismiss() }) {
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


