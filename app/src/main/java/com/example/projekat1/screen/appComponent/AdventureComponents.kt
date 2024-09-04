package com.example.projekat1.screen.appComponent

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun UploadAdventureImages(
    selectedImages: MutableState<List<Uri>>,
    onImagesSelected: (List<Uri>) -> Unit
) {
    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris != null) {
            // Ažurirajte listu slika i obavestite o promeni
            val updatedImages = (selectedImages.value + uris).distinct()
            selectedImages.value = updatedImages
            onImagesSelected(updatedImages)
        }
    }

    LazyRow {
        if (selectedImages.value.size < 5) {
            item {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .width(100.dp)
                        .height(100.dp)
                        .border(
                            1.dp,
                            color = Color(0xFFC1C1C1),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            color = Color(0xFFC1C1C1),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { pickImagesLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.AddAPhoto, contentDescription = "Add photo")
                }
            }
        }

        items(selectedImages.value.size) { index ->
            val uri = selectedImages.value[index]
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .width(100.dp)
                    .height(100.dp)
                    .border(
                        1.dp,
                        Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        // Uklonite sliku iz liste
                        val updatedImages = selectedImages.value - uri
                        selectedImages.value = updatedImages
                        onImagesSelected(updatedImages)
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = "Selected image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }
    }
}





@Composable
fun LevelSelection(
    selectedLevel: String,
    onLevelSelected: (String) -> Unit
) {
    val levels = listOf("Easy", "Moderate", "Hard")
    val selectedColor = Color(0xFF6A9F5B) // Subtle matte green
    val unselectedColor = Color.Gray // Gray for unselected items

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween, // Distribute evenly across the row
        verticalAlignment = Alignment.CenterVertically // Center vertically
    ) {
        levels.forEach { level ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 4.dp) // Minimal padding
                    .clickable { onLevelSelected(level) } // Handle click
            ) {
                RadioButton(
                    selected = (level == selectedLevel),
                    onClick = null, // Disable internal click handling
                    modifier = Modifier.size(16.dp), // Smaller radio buttons
                    colors = RadioButtonDefaults.colors(
                        selectedColor = selectedColor, // Apply subtle green when selected
                        unselectedColor = unselectedColor // Gray for unselected
                    )
                )
                Spacer(modifier = Modifier.width(4.dp)) // Space between radio button and text
                Text(
                    text = level,
                    style = MaterialTheme.typography.body2.copy( // Slightly smaller text
                        color = if (level == selectedLevel) selectedColor else Color.Black
                    )
                )
            }
        }
    }
}






