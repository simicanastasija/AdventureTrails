package com.example.projekat1.viewModel

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projekat1.models.Adventure
import com.example.projekat1.repositories.AdventureRepositoryImpl
import com.example.projekat1.repositories.Resource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdventureViewModel : ViewModel() {

    val repository = AdventureRepositoryImpl()
    private val _adventureFlow = MutableStateFlow<Resource<String>?>(null)
    val adventureFlow: StateFlow<Resource<String>?> = _adventureFlow
    private val _adventures = MutableStateFlow<Resource<List<Adventure>>>(Resource.Success(emptyList()))
    val adventures: StateFlow<Resource<List<Adventure>>> get() = _adventures
    private val _userAdventures = MutableStateFlow<Resource<List<Adventure>>>(Resource.Success(emptyList()))
    val userAdventures: StateFlow<Resource<List<Adventure>>> get() = _userAdventures

    val _userFullNames = mutableMapOf<String, String>()

    private val _adventureImages = mutableStateMapOf<String, List<String>>()
    val adventureImages: Map<String, List<String>> get() = _adventureImages

    init {
        getAllAdventures()
    }
    fun getAllAdventures() = viewModelScope.launch {
        _adventures.value = repository.getAllAdventures()
        val adventureList = (adventures.value as? Resource.Success<List<Adventure>>)?.result ?: emptyList()

        adventureList.forEach { adventure ->
            if (!_userFullNames.containsKey(adventure.userId)) {
                val fullNameResource = repository.getUserFullName(adventure.userId)
                if (fullNameResource is Resource.Success) {
                    _userFullNames[adventure.userId] = fullNameResource.result
                }
            }
            if (adventure.adventureImages.isNotEmpty()) {
                _adventureImages[adventure.id] =
                    listOf(adventure.adventureImages[0]) // Assuming the first image URL is used
            }
        }
    }


    fun saveAdventure(
        location: MutableState<LatLng?>,
        title: String,
        description: String,
        type: String,
        level: String,
        adventureImages : List<Uri>
    ) = viewModelScope.launch{
        _adventureFlow.value = Resource.Loading
        repository.saveAdventure(
            location = location.value!!,
            title = title,
            description = description,
            type = type,
            level = level,
            adventureImages = adventureImages
        )
        _adventureFlow.value = Resource.Success("Avantura je uspesno dodata!")
    }

    fun getUserAdventures(
        uid: String
    ) = viewModelScope.launch {
        _userAdventures.value = repository.getUserAdventures(uid)
    }
}

class AdventureViewModelFactory: ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AdventureViewModel::class.java)){
            return AdventureViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}