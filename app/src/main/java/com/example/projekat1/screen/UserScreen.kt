package com.example.projekat1.screen

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import com.example.projekat1.location.LocationService
import com.example.projekat1.viewModel.UserAuthViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserScreen(
    navController: NavController,
    viewModel: UserAuthViewModel,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    myLocation: MutableState<LatLng?> = remember { mutableStateOf(null) }
) {


    val context = LocalContext.current
    // val showDialog = remember { mutableStateOf(false) }


    Log.d("MapScreen", "MapScreen Composable Started")

    // Check location permissions and start LocationService
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.d("MapScreen", "Permissions not granted, requesting permissions")
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )
    } else {
        // Start LocationService
        Log.d("MapScreen", "Permissions granted, starting LocationService")

        Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            context.startForegroundService(this)
        }
    }

    // Register BroadcastReceiver to receive location updates
    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Log.d("MapScreen", "BroadcastReceiver received update")

                if (intent?.action == LocationService.ACTION_LOCATION_UPDATE) {
                    val latitude =
                        intent.getDoubleExtra(LocationService.EXTRA_LOCATION_LATITUDE, 0.0)
                    val longitude =
                        intent.getDoubleExtra(LocationService.EXTRA_LOCATION_LONGITUDE, 0.0)
                    //azuriranje camera position
                    myLocation.value = LatLng(latitude, longitude)
                    //cameraPositionState.position =
                    //CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 17f)
//                    Log.d(
//                        "MapScreen",
//                        "Location updated: Latitude: $latitude, Longitude: $longitude"
//                    )
                }
            }
        }
    }

    // Register the receiver
    DisposableEffect(context) {
        //Log.d("MapScreen", "Registering BroadcastReceiver")

        LocalBroadcastManager.getInstance(context)
            .registerReceiver(receiver, IntentFilter(LocationService.ACTION_LOCATION_UPDATE))
        onDispose {
            //Log.d("MapScreen", "Unregistering BroadcastReceiver")

            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        }
    }

    LaunchedEffect(myLocation.value) {//dodala launched effect
        myLocation.value?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 17f)
//            Log.d(
//                "MapScreen",
//                "CameraPosition updated to: Latitude: ${location.latitude}, Longitude: ${location.longitude}"
//            )

        }
    }


        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                modifier = Modifier.fillMaxSize()
            ) {
                myLocation.value?.let { location ->
                    Log.d(
                       "LocationUpdate",
                       "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                   )
                    Marker(
                        state = MarkerState(position = location),
                        title = "You are here",
                        snippet = "Current location of the user",
                        onClick = {

                            //showDialog.value = true
                            true
                        }
                    )
                }


            }
            // Dialog for adding a book
//    if (showDialog.value) {
//        CustomDialog(
//            showDialog = showDialog,
//            onAddBookClick = {
//                Log.d("MapScreen", "Add new book button clicked")
//
//            }
//        )
//
//    }
        }
    }





