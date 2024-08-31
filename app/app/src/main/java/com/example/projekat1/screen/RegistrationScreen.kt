package com.example.projekat1.screen

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.projekat1.R
import com.example.projekat1.navigation.Routes
import com.example.projekat1.repositories.Resource
import com.example.projekat1.screen.components.Alternative
import com.example.projekat1.screen.components.CustomInput
import com.example.projekat1.screen.components.CustomLabel
import com.example.projekat1.screen.components.Password
import com.example.projekat1.screen.components.RegisterButton
import com.example.projekat1.screen.components.UploadProfileImg
import com.example.projekat1.viewModel.UserAuthViewModel

@Composable
fun RegistrationScreen(
    viewModel: UserAuthViewModel?,
    navController: NavController?
) {
    val signUpFlow = viewModel?.signUpFlow?.collectAsState()

    val email = remember { mutableStateOf("") }
    val isEmailError = remember { mutableStateOf(false) }
    val emailErrorText = remember { mutableStateOf("") }

    val password = remember { mutableStateOf("") }
    val isPasswordError = remember { mutableStateOf(false) }
    val passwordErrorText = remember { mutableStateOf("") }

    val fullName = remember { mutableStateOf("") }
    val isFullNameError = remember { mutableStateOf(false) }

    val phoneNumber = remember { mutableStateOf("") }
    val isPhoneNumberError = remember { mutableStateOf(false) }

    val profileImg = remember { mutableStateOf(Uri.EMPTY) }
    val isIProfileImgError = remember { mutableStateOf(false) }

    val showPassword = remember { mutableStateOf(false) }

    val buttonEnabled = remember { mutableStateOf(true) }
    val isLoading = remember { mutableStateOf(false) }
    val isError = remember { mutableStateOf(false) }
    val errorText = remember { mutableStateOf("") }

    val backgroundImage: Painter = painterResource(id = R.drawable.pozadina) // Pozadinska slika

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
                .background(Color(0x30000000)) // Poluprovidna pozadina
                .padding(start = 30.dp, end = 30.dp, top = 14.dp, bottom = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xE6F0F8E7), shape = RoundedCornerShape(16.dp)) // Svetlo mat zeleni okvir
                    .border(1.dp, Color(0xFF9CDB8E), shape = RoundedCornerShape(16.dp)) // Okvir boje
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // centriram naslov
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Join the Adventure",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                CustomLabel(label = "Create your account to explore and share beautiful stories...")

                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color(0xFFBCAAA4),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(2.dp))
                        CustomLabel(label = "Choose your profile picture by clicking on the icon below:")
                        Spacer(modifier = Modifier.height(8.dp))
                        UploadProfileImg(profileImg, isIProfileImgError)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                CustomLabel(label = "Full name")

                Spacer(modifier = Modifier.height(2.dp))
                CustomInput(
                    hint = "Anastasija Simic",
                    value = fullName,
                    isEmail = false,
                    isError = isFullNameError,
                    errorText = emailErrorText,
                    backgroundColor = Color.Transparent // Transparent background
                )

                Spacer(modifier = Modifier.height(16.dp))
                CustomLabel(label = "Email address")

                Spacer(modifier = Modifier.height(2.dp))
                CustomInput(
                    hint = "anastasija@gmail.com",
                    value = email,
                    isEmail = true,
                    isError = isEmailError,
                    errorText = emailErrorText,
                    backgroundColor = Color.Transparent // Transparent background
                )

                Spacer(modifier = Modifier.height(16.dp))
                CustomLabel(label = "Phone number")

                Spacer(modifier = Modifier.height(2.dp))
                CustomInput(
                    hint = "+381602600626",
                    value = phoneNumber,
                    isEmail = false,
                    isError = isPhoneNumberError,
                    errorText = emailErrorText,
                    backgroundColor = Color.Transparent // Transparent background
                )

                Spacer(modifier = Modifier.height(16.dp))
                CustomLabel(label = "Password")

                Spacer(modifier = Modifier.height(2.dp))
                Password(
                    inputValue = password,
                    isError = isPasswordError,
                    errorText = passwordErrorText,
                    backgroundColor = Color.Transparent, // Transparent background
                    hint = "bilosta"
                )

                Spacer(modifier = Modifier.height(2.dp))
                RegisterButton(
                    onClick = {
                        isIProfileImgError.value = false
                        isEmailError.value = false
                        isPasswordError.value = false
                        isFullNameError.value = false
                        isPhoneNumberError.value = false
                        isError.value = false
                        isLoading.value = true

                        if (profileImg.value == Uri.EMPTY) {
                            isIProfileImgError.value = true
                            isLoading.value = false
                        } else if (fullName.value.isEmpty()) {
                            isFullNameError.value = true
                            isLoading.value = false
                        } else if (email.value.isEmpty()) {
                            isEmailError.value = true
                            isLoading.value = false
                        } else if (phoneNumber.value.isEmpty()) {
                            isPhoneNumberError.value = true
                            isLoading.value = false
                        } else if (password.value.isEmpty()) {
                            isPasswordError.value = true
                            isLoading.value = false
                        } else {
                            viewModel?.register(
                                profileImg = profileImg.value,
                                fullName = fullName.value,
                                email = email.value,
                                phoneNumber = phoneNumber.value,
                                password = password.value
                            )
                        }
                    },
                    buttonText = "Sign Up",
                    isEnabled = buttonEnabled,
                    isLoading = isLoading,
                    //backgroundColor = Color(0xFF4CAF50) // Tamnije mat zelena boja
                )

                Spacer(modifier = Modifier.height(17.dp))
                Alternative(
                    text = "Already have an account? ",
                    link = "Sign In",
                    onClick = {
                        navController?.navigate(Routes.loginScreen)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                signUpFlow?.value?.let {
                    when (it) {
                        is Resource.Failure -> {
                            isLoading.value = false
                            Log.e("[ERROR]", it.exception.message.toString())
                        }

                        is Resource.Success -> {
                            isLoading.value = false
                            LaunchedEffect(Unit) {
                                navController?.navigate(Routes.loginScreen) {
                                    popUpTo(Routes.mapScreen) {
                                        inclusive = true
                                    }
                                }
                            }
                        }

                        is Resource.Loading -> {}

                        null -> Log.d("SignUpScreen", "SignUp flow doesn't exist!")
                        is Resource.Failure -> TODO()
                        Resource.Loading -> TODO()
                        is Resource.Success -> TODO()
                    }
                }
            }
        }
    }
}



