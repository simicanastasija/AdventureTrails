package com.example.projekat1.screen.appComponent

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
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
fun LevelDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    selectedLevel: String,
    onLevelSelected: (String) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf("Easy", "Moderate", "Hard").forEach { levelOption ->
            DropdownMenuItem(
                onClick = {
                    onLevelSelected(levelOption)
                    onDismissRequest()
                }
            ) {
                Text(text = levelOption)
            }
        }
    }
}