package com.example.projekat1.repositories

import com.google.android.gms.maps.model.LatLng
import android.net.Uri
import com.example.projekat1.models.Adventure

interface AdventureRepository {

    suspend fun saveAdventure (
        location: LatLng,
        title: String,
        description: String,
        type: String,
        level: String,
        adventureImages: List<Uri>

    ): Resource<String>

    suspend fun getAllAdventures(): Resource<List<Adventure>>
    suspend fun getUserAdventures(
        uid: String
    ): Resource<List<Adventure>>

    suspend fun getUserFullName(userId: String): Resource<String>
}