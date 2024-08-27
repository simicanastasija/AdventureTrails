package com.example.projekat1.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.projekat1.R
import com.example.projekat1.models.User
import com.example.projekat1.navigation.Routes
import com.example.projekat1.repositories.Resource
import com.example.projekat1.screen.components.Alternative
import com.example.projekat1.screen.components.BSBackground
import com.example.projekat1.screen.components.CustomInput
import com.example.projekat1.screen.components.CustomLabel
import com.example.projekat1.screen.components.Heading
import com.example.projekat1.screen.components.Heading2
import com.example.projekat1.viewModel.UserAuthViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun LoginScreen(
    viewModel: UserAuthViewModel,
    navController: NavController
) {
    val email = remember { mutableStateOf("") }
    val isEmailError = remember { mutableStateOf(false) }
    val emailErrorText = remember { mutableStateOf("") }

    val password = remember { mutableStateOf("") }
    val isPasswordError = remember { mutableStateOf(false) }
    val passwordErrorText = remember { mutableStateOf("") }

    val isLoading = remember { mutableStateOf(false) }
    val signInFlow = viewModel.signInFlow.collectAsState()

    val backgroundImage: Painter = painterResource(id = R.drawable.pozadina) // Background image

    val currentUser = remember {
        mutableStateOf<User?>(null)
    }
    val currUserData = viewModel.currentUserFlow.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x30000000)) // Semi-transparent background
                .padding(start = 30.dp, end = 30.dp, top = 14.dp, bottom = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xE6F0F8E7), shape = RoundedCornerShape(16.dp)) // Light matte green
                    .border(1.dp, Color(0xFF9CDB8E), shape = RoundedCornerShape(16.dp)) // Border
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Centered title
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Step into Your Next Adventure",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                CustomLabel(label = "Email Address")
                CustomInput(
                    hint = "example@domain.com",
                    value = email,
                    isEmail = true,
                    isError = isEmailError,
                    errorText = emailErrorText,
                    backgroundColor = Color.Transparent
                )

                Spacer(modifier = Modifier.height(16.dp))
                CustomLabel(label = "Password")
                CustomInput(
                    hint = "********",
                    value = password,
                    isEmail = false,
                    isError = isPasswordError,
                    errorText = passwordErrorText,
                    backgroundColor = Color.Transparent
                )

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        isEmailError.value = false
                        isPasswordError.value = false
                        isLoading.value = true

                        if (email.value.isEmpty()) {
                            isEmailError.value = true
                            isLoading.value = false
                        } else if (password.value.isEmpty()) {
                            isPasswordError.value = true
                            isLoading.value = false
                        } else {
                            viewModel.logIn(
                                email = email.value,
                                password = password.value
                            )

                        }
                    },
                    enabled = !isLoading.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6D4C41),
                        contentColor = Color(0xFF3C0B1A)
                    )
                ) {
                    Text(
                        text = "Log In",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDC9AF),
                        )
                    )
                }

                Spacer(modifier = Modifier.height(17.dp))
                Alternative(
                    text = "New to Adventure Trails? Join now  ",
                    link = "Register",
                    onClick = {
                        navController.navigate(Routes.registrationScreen)
                    }
                )
            }

            /*signInFlow.value?.let {
                when (it) {
                    is Resource.Failure -> {
                        isLoading.value = false
                        Log.e("[ERROR]", it.exception.message.toString())
                    }
                    is Resource.Success -> {
                        isLoading.value = false
                        LaunchedEffect(Unit) {
                            navController.navigate(Routes.mapScreen) {
                                popUpTo(Routes.loginScreen) { inclusive = true }
                            }
                        }
                    }
                    is Resource.Loading -> { /* Show loading indicator if needed */ }
                }
            }*/
            LaunchedEffect(signInFlow.value) {
                when (val result = signInFlow.value) {
                    is Resource.Failure -> {
                        isLoading.value = false
                        //Log.d("[ERROR]", result.exception.message.toString())
                    }
                    is Resource.Success -> {
                        Log.d("[DEBUG]", "Login successful, fetching user data.")
                        viewModel.getUserData()
                    }
                    is Resource.Loading -> {
                        isLoading.value = true
                        Log.d("[DEBUG]", "Loading...")
                    }
                    null -> {
                        Log.d("[DEBUG]", "signInFlow is null")
                    }
                }
            }

            LaunchedEffect(currUserData.value) {
                when (val userResource = currUserData.value) {
                    is Resource.Success -> {
                        Log.d("[DEBUG]", "User data fetched successfully.")
                        val user = userResource.result
                        isLoading.value = false


                        val currUserJSON = Gson().toJson(user)
                        //val encodedUsr = URLEncoder.encode(currUserJSON, StandardCharsets.UTF_8.toString())

                        navController.navigate(Routes.mapScreen) {
                            popUpTo(Routes.loginScreen) { inclusive = true }
                        }

                    }
                    is Resource.Failure -> {
                        Log.d("[DEBUG]", "Failed to fetch user data.")
                        currentUser.value = null
                        isLoading.value = false
                    }
                    is Resource.Loading -> {
                        isLoading.value = true
                        Log.d("[DEBUG]", "Loading user data...")
                    }
                    null -> {
                        Log.d("[DEBUG]", "currUserData is null")
                    }
                }
            }
        }
    }
}




