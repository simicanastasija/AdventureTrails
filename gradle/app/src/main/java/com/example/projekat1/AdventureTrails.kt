package com.example.projekat1

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.projekat1.viewModel.UserAuthViewModel
import com.example.projekat1.navigation.Router
import com.example.projekat1.viewModel.AdventureViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdventureTrails(viewModel: UserAuthViewModel, adventureViewModel: AdventureViewModel){
    Surface(modifier = Modifier.fillMaxSize()){
        Router(viewModel, adventureViewModel)
    }
}