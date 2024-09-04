package com.example.projekat1.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projekat1.screen.AdventureDetailsScreen
import com.example.projekat1.screen.FiltersScreen
import com.example.projekat1.viewModel.UserAuthViewModel
import com.example.projekat1.screen.LoginScreen
import com.example.projekat1.screen.RegistrationScreen
import com.example.projekat1.screen.MapScreen
import com.example.projekat1.screen.ProfileScreen
import com.example.projekat1.screen.RankScreen
import com.example.projekat1.screen.TableScreen
import com.example.projekat1.viewModel.AdventureViewModel
import com.google.android.gms.maps.model.LatLng
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


        composable(Routes.profileScreen) {
            ProfileScreen(    viewModel= adventureViewModel,
                userAuthViewModel=  viewModel,
                navController= navController)
        }

        composable(Routes.tableScreen) {
            TableScreen(adventureViewModel = adventureViewModel, navController= navController)
        }

        composable(
            route = "adventureDetailsScreen/{adventureId}",
            arguments = listOf(navArgument("adventureId") { type = NavType.StringType })
        ) { backStackEntry ->
            val adventureId = backStackEntry.arguments?.getString("adventureId")
            AdventureDetailsScreen(
                navController = navController,
                adventureId = adventureId ?: "",
                adventureViewModel = adventureViewModel, // Pass the appropriate ViewModel
                viewModel = viewModel
            )
        }

        composable(Routes.rankScreen){
            RankScreen(navController = navController, userViewModel = viewModel)
        }

        composable(Routes.filtersScreen + "/{latitude}/{longitude}") { backStackEntry ->
            val latitudeString = backStackEntry.arguments?.getString("latitude") ?: "0.0"
            val longitudeString = backStackEntry.arguments?.getString("longitude") ?: "0.0"

            // Konvertujte String u Double
            val latitude = latitudeString.toDoubleOrNull() ?: 0.0
            val longitude = longitudeString.toDoubleOrNull() ?: 0.0

            // Kreirajte LatLng objekat
            val mapLocation = LatLng(latitude, longitude)

            val myLocation = remember { mutableStateOf<LatLng?>(mapLocation) }


            FiltersScreen(
                navController = navController,
                onApplyFilters = { filters ->
                    adventureViewModel.applyFilters(
                        filters = filters,
                        userLocation = myLocation
                    )
                },

            )
        }




    }
}

