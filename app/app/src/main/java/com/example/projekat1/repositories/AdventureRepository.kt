package com.example.projekat1.repositories

import com.google.android.gms.maps.model.LatLng
import android.net.Uri
import com.example.projekat1.models.Adventure
import com.example.projekat1.models.Comment
import kotlinx.coroutines.flow.Flow

interface AdventureRepository {

    suspend fun saveAdventure (
        location: LatLng,
        title: String,
        description: String,
        type: String,
        level: String,
        adventureImages: List<Uri>,
        //checked: "yes"

    ): Resource<String>

    suspend fun getAllAdventures(): Resource<List<Adventure>>
    suspend fun getUserAdventures(
        uid: String
    ): Resource<List<Adventure>>

    suspend fun getUserFullName(userId: String): Resource<String>
    fun getAdventureById(adventureId: String): Flow<Resource<Adventure>>


    //suspend fun getCommentsForAdventure(bookId: String): List<Comment>
    suspend fun addCommentToAdventure(uid: String, adventureId: String, comment: Comment)

}