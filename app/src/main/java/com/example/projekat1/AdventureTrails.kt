package com.example.projekat1

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.projekat1.viewModel.UserAuthViewModel
import com.example.projekat1.navigation.Router

@Composable
fun AdventureTrails(viewModel: UserAuthViewModel){
    Surface(modifier = Modifier.fillMaxSize()){
        Router(viewModel)
    }
}