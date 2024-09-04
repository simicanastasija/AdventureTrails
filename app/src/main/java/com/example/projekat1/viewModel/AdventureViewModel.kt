package com.example.projekat1.viewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.Insets.add
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.example.projekat1.models.Adventure
import com.example.projekat1.models.Comment
import com.example.projekat1.repositories.AdventureRepositoryImpl
import com.example.projekat1.repositories.Resource
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class AdventureViewModel : ViewModel() {

    val repository = AdventureRepositoryImpl()
    private val _adventureFlow = MutableStateFlow<Resource<String>?>(null)
    val adventureFlow: StateFlow<Resource<String>?> = _adventureFlow
    private val _adventures =
        MutableStateFlow<Resource<List<Adventure>>>(Resource.Success(emptyList()))
    val adventures: StateFlow<Resource<List<Adventure>>> get() = _adventures
    private val _userAdventures =
        MutableStateFlow<Resource<List<Adventure>>>(Resource.Success(emptyList()))
    val userAdventures: StateFlow<Resource<List<Adventure>>> get() = _userAdventures

    val _userFullNames = mutableMapOf<String, String>()

    private val _adventureImages = mutableStateMapOf<String, List<String>>()
    val adventureImages: Map<String, List<String>> get() = _adventureImages

    private val _adventureState = MutableStateFlow<Resource<Adventure>>(Resource.Loading)
    val adventureState: StateFlow<Resource<Adventure>> = _adventureState

    private val _specificAdventureComments = MutableStateFlow<List<Comment>>(emptyList())
    val specificAdventureComments: StateFlow<List<Comment>> = _specificAdventureComments

    init {
        getAllAdventures()
    }


   private val _filteredAdventures = MutableStateFlow<List<Adventure>>(emptyList())
    val filteredAdventures: StateFlow<List<Adventure>> get() = _filteredAdventures



    fun applyFilters(filters: Map<String, String>, userLocation: MutableState<LatLng?>) {
        val filteredAdventures = _adventures.value.let { resource ->
            if (resource is Resource.Success) {
                resource.result.filter { adventure ->
                    val matchesType = filters["type"]?.let { adventure.type == it } ?: true
                    val matchesLevel = filters["level"]?.let { adventure.level == it } ?: true
                    val matchesCommentRange = filters["comments"]?.let {
                        val range = it as String
                        val (min, max) = range.split("-").map { it.toInt() }
                        val commentCount = adventure.comments.size
                        commentCount in min..max
                    } ?: true
                    val matchesVisitorCount = filters["visitors"]?.let { filterCount ->
                        adventure.visitedUsers.size >= filterCount.toInt()//.toInt()
                    } ?: true

                    // Dodaj radijus filtriranje
                    val matchesRadius = filters["radius"]?.let { filterRadius ->
                        val distance = calculateDistance(userLocation, adventure.location) // Lokacija avanture kao GeoPoint
                        distance <= filterRadius.toFloat() // Proveri da li je udaljenost unutar radijusa
                    } ?: true


                    matchesType && matchesLevel && matchesCommentRange && matchesVisitorCount && matchesRadius
                }
            } else {
                emptyList()
            }
        }
        _adventures.value = Resource.Success(filteredAdventures)
    }


    fun calculateDistance(start: MutableState<LatLng?>, end: GeoPoint): Float {
        val startLatLng = start.value ?: return Float.MAX_VALUE

        val endLatLng = LatLng(end.latitude, end.longitude)
        val earthRadius = 6371 // Earth radius in kilometers

        val lat1 = Math.toRadians(startLatLng.latitude)
        val lon1 = Math.toRadians(startLatLng.longitude)
        val lat2 = Math.toRadians(endLatLng.latitude)
        val lon2 = Math.toRadians(endLatLng.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2) +
                cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return (earthRadius * c).toFloat()
    }


    fun getAllAdventures() = viewModelScope.launch {
        _adventures.value = repository.getAllAdventures()
        val adventureList =
            (adventures.value as? Resource.Success<List<Adventure>>)?.result ?: emptyList()

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
        adventureImages: List<Uri>
    ) = viewModelScope.launch {
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

    fun getAdventureById(adventureId: String, userId: String) {
        viewModelScope.launch {
            repository.getAdventureById(adventureId).collect { resource ->
                if (resource is Resource.Success) {
                    val adventure = resource.result
                    // Provjerite je li avantura posjećena od strane trenutnog korisnika
                    val isVisited = adventure.visitedUsers.contains(userId)
                    _adventureState.value = Resource.Success(adventure.copy(isVisited = isVisited))
                } else {
                    _adventureState.value = resource
                }
            }
        }
    }

    fun addComment(uid: String, adventureId: String, comment: Comment) = viewModelScope.launch {
        try {
            // Dodavanje komentara u repozitorijum (npr. Firestore)
            repository.addCommentToAdventure(uid, adventureId, comment)

            // Ažuriraj listu komentara u ViewModelu nakon dodavanja komentara
            val updatedComments = _specificAdventureComments.value.toMutableList().apply {
                add(comment)
            }
            _specificAdventureComments.value = updatedComments
        } catch (e: Exception) {
            Log.e("AdventureViewModel", "Error adding comment to adventure", e)
        }
    }

    fun loadCommentsForAdventure(adventureId: String) = viewModelScope.launch {
        try {
            _specificAdventureComments.value = repository.getCommentsForAdventures(adventureId)
        } catch (e: Exception) {
            Log.e("BookViewModel", "Error loading comments for book", e)
        }
    }


    fun markAdventureAsVisited(adventureId: String, userId: String, level: String) {
        viewModelScope.launch {
            try {
                repository.markAdventureAsVisited(adventureId, userId, level)
                getAdventureById(adventureId, userId)

            } catch (e: Exception) {
                Log.e("AdventureViewModel", "Error marking adventure as visited", e)
            }
        }
    }

}


    class AdventureViewModelFactory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdventureViewModel::class.java)) {
                return AdventureViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
