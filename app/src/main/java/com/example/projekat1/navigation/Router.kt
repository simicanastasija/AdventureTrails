package com.example.projekat1.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projekat1.viewModel.UserAuthViewModel
import com.example.projekat1.screen.LoginScreen
import com.example.projekat1.screen.RegistrationScreen
import com.example.projekat1.screen.MapScreen
import com.example.projekat1.screen.ProfileScreen
import com.example.projekat1.screen.SettingsScreen
import com.example.projekat1.screen.TableScreen
import com.example.projekat1.viewModel.AdventureViewModel
import com.google.maps.android.compose.rememberCameraPositionState


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Router ( viewModel: UserAuthViewModel, adventureViewModel : AdventureViewModel)
{
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.loginScreen) {
        composable(Routes.registrationScreen) {
            RegistrationScreen(viewModel = viewModel, navController = navController)
        }
        composable(Routes.loginScreen) {
            LoginScreen(viewModel = viewModel, navController = navController)
        }

        composable(Routes.mapScreen) {
            MapScreen(viewModel = viewModel, adventureViewModel = adventureViewModel, navController = navController,
                cameraPositionState = rememberCameraPositionState(),
                myLocation = remember { mutableStateOf(null) })
        }

        composable(Routes.settingsScreen){
            SettingsScreen(navController = navController, viewModel = viewModel)
        }

        composable(Routes.profileScreen) {
            ProfileScreen(    viewModel= adventureViewModel,
                userAuthViewModel=  viewModel,
                navController= navController)
        }

        composable(Routes.tableScreen) {
            TableScreen(adventureViewModel = adventureViewModel, navController= navController)
        }
    }
}