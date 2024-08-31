package com.example.projekat1.screen


import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projekat1.navigation.Routes
import com.example.projekat1.location.LocationService
import com.example.projekat1.viewModel.UserAuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: UserAuthViewModel
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val isTrackingServiceEnabled = sharedPreferences.getBoolean("tracking_location", false)

    val checked = remember {
        mutableStateOf(isTrackingServiceEnabled)
    }

    fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    val isServiceRunning = isServiceRunning(LocationService::class.java)



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF3E0))
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFE5D6B3)) // Set the header background color
                .padding(horizontal = 8.dp, vertical = 4.dp) // Reduced padding
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Center: Title
                androidx.compose.material.Text(
                    text = "AdventureTrails",
                    style = MaterialTheme.typography.h6.copy(fontSize = 18.sp) // Smaller font size
                )
            }

            Spacer(modifier = Modifier.height(32.dp))


            Text(
                text = "SERVICE FOR TRACKING",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF6F4F28),
                    fontStyle = FontStyle.Italic
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tracking services box
            Box(
                modifier = Modifier
                    .width(350.dp) // Postavite željenu širinu
                    .align(Alignment.CenterHorizontally) // Centrirajte Box horizontalno
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Tracking services",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF6F4F28)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(5.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Adventure nearby!",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color(0xFF6F4F28)
                            )
                        )
                        Switch(
                            checked = checked.value,
                            onCheckedChange = {
                                checked.value = it
                                if (it) {
                                    Intent(context, LocationService::class.java).apply {
                                        action = LocationService.ACTION_FIND_NEARBY
                                        context.startForegroundService(this)
                                    }
                                    with(sharedPreferences.edit()) {
                                        putBoolean("tracking_location", true)
                                        apply()
                                    }
                                } else {
                                    Intent(context, LocationService::class.java).apply {
                                        action = LocationService.ACTION_STOP
                                        context.stopService(this)
                                        Log.d("ServiceSettings", "Stop action sent")
                                    }
                                    //kad odkomentarisem i dalje radi pracenje lokacije u pozadini, a knjige u blizini ne, sto i treba
                                    Intent(context, LocationService::class.java).apply {
                                        action = LocationService.ACTION_START
                                        context.startForegroundService(this)
                                        Log.d("ServiceSettings", "Start action sent")
                                    }
                                    with(sharedPreferences.edit()) {
                                        putBoolean("tracking_location", false)
                                        apply()
                                    }
                                }
                            },
                            thumbContent = if (checked.value) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            } else {
                                null
                            }
                        )
                        androidx.compose.material.Button(
                            onClick = {
                                viewModel.logOut()
                                navController.navigate("loginScreen") {
                                    popUpTo("profileScreen") { inclusive = true }
                                }
                            },
                            colors = androidx.compose.material.ButtonDefaults.buttonColors(
                                backgroundColor = Color.Red
                            )
                        ) {
                            androidx.compose.material.Text("Log Out", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}