package com.example.projekat1.screen.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.projekat1.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory


val LightGreen = Color(0xFF8BC34A)
val DarkGreen = Color(0xFF4CAF50)
val LightBlue = Color(0xFF03A9F4)
val DarkBlue = Color(0xFF0288D1)

@Composable
fun StartImage(content: @Composable () -> Unit) {
    val backgroundImage: Painter = painterResource(id = R.drawable.pozadina)
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000)) // Poluprovidna crna pozadina
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun Alternative(text: String, link: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = text, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = link,
            color = DarkBlue,
            modifier = Modifier.clickable(onClick = onClick)
        )
    }
}

@Composable
fun CustomInput(
    hint: String,
    value: MutableState<String>,
    isEmail: Boolean,
    isError: MutableState<Boolean>,
    errorText: MutableState<String>,
    backgroundColor: Color
) {
    Column {
        TextField(
            value = value.value,
            onValueChange = { value.value = it },
            label = { Text(text = hint) },
            isError = isError.value,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .border(1.dp, LightGreen, RoundedCornerShape(8.dp)) // Granica u svetlo zelenoj boji
        )
        if (isError.value) {
            Text(
                text = errorText.value,
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun Password(
    inputValue: MutableState<String>,
    hint: String,
    isError: MutableState<Boolean>,
    errorText: MutableState<String>,
    backgroundColor: Color
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        TextField(
            value = inputValue.value,
            onValueChange = { inputValue.value = it },
            label = { Text(text = hint) },
            isError = isError.value,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, LightGreen, RoundedCornerShape(8.dp)), // Granica u svetlo zelenoj boji
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )
        if (isError.value) {
            Text(
                text = errorText.value,
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}


@Composable
fun RegisterButton(
    onClick: () -> Unit,
    buttonText: String,
    isEnabled: MutableState<Boolean>,
    isLoading: MutableState<Boolean>
) {
    Button(
        onClick = onClick,
        enabled = isEnabled.value && !isLoading.value,
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkGreen, shape = RoundedCornerShape(50.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen, contentColor = Color.White)
    ) {
        if (isLoading.value) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Text(text = buttonText)
        }
    }
}

@Composable
fun UploadProfileImg(
    selectedImg: MutableState<Uri?>,
    isError: MutableState<Boolean>
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImg.value = uri
        }
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selectedImg.value == Uri.EMPTY || selectedImg.value == null) {
            Image(
                painter = painterResource(id = R.drawable.pozadina),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(140.dp)
                    .border(
                        if (isError.value) BorderStroke(2.dp, Color.Red) else BorderStroke(
                            0.dp,
                            Color.Transparent
                        )
                    )
                    .clip(RoundedCornerShape(70.dp)) // 50% border radius
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
        } else {
            selectedImg.value?.let { uri ->
                Image(
                    painter = painterResource(id = R.drawable.pozadina),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(140.dp)
                        .border(
                            if (isError.value) BorderStroke(2.dp, Color.Red) else BorderStroke(
                                0.dp,
                                Color.Transparent
                            )
                        )
                        .clip(RoundedCornerShape(70.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                )
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(70.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun Heading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun CustomLabel(label: String) {
    Text(text = label, color = Color.Black)
}

@Composable
fun BSBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue) // Pozadina u svetlo plavoj boji
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun Heading2(secondary_text: String) {
    Text(
        text = secondary_text,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

fun bitmapDescriptorFromVector2(context: Context, vectorResId: Int): BitmapDescriptor? {
    return ContextCompat.getDrawable(context, vectorResId)?.run {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        draw(Canvas(bitmap))
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
