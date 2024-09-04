package com.example.projekat1.repositories

import android.net.Uri
import android.util.Log
import com.example.projekat1.models.Adventure
import com.example.projekat1.models.Comment
import com.example.projekat1.services.DbService
import com.example.projekat1.services.StorageService
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AdventureRepositoryImpl: AdventureRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val storageInstance = FirebaseStorage.getInstance()

    private val databaseService = DbService(firestoreInstance)
    private val storageService = StorageService(storageInstance)

    override suspend fun saveAdventure(
        location: LatLng,
        title: String,
        description: String,
        type: String,
        level: String,
        adventureImages: List<Uri>
    ): Resource<String> {
        return try{
            val currentUser = firebaseAuth.currentUser
            if(currentUser!=null){
                val adventureImagesUrls = storageService.uploadAdventureImages(adventureImages)
                val geoLocation = GeoPoint(
                    location.latitude,
                    location.longitude
                )
                val user = currentUser.uid
                val newAdventure = Adventure(
                    userId = user,
                    location = geoLocation,
                    title = title,
                    description = description,
                    type = type,
                    level = level,
                    adventureImages = adventureImagesUrls
                )
                databaseService.saveAdventure(newAdventure)
                databaseService.updateUserPoints(user, 15) //kada korisnik doda na mapi neki objekat +15 poena
            }
            Resource.Success("Uspesno su sačuvani svi podaci o avanturi")
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getAllAdventures(): Resource<List<Adventure>> {
        return try{
            val snapshot = firestoreInstance.collection("adventures").get().await()
            val adventures = snapshot.toObjects(Adventure::class.java)
            Resource.Success(adventures)
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getUserAdventures(uid: String): Resource<List<Adventure>> {
        return try {
            val snapshot = firestoreInstance.collection("adventures")
                .whereEqualTo("userId", uid)
                .get()
                .await()
            val adventures = snapshot.toObjects(Adventure::class.java)
            Resource.Success(adventures)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getUserFullName(userId: String): Resource<String> {
        return try {
            val document = firestoreInstance.collection("users").document(userId).get().await()
            val fullName = document.getString("fullName") ?: "Unknown User"
            Resource.Success(fullName)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override fun getAdventureById(adventureId: String): Flow<Resource<Adventure>> = flow {
        emit(Resource.Loading)
        try {
            val adventureSnapshot = firestoreInstance.collection("adventures").document(adventureId).get().await()
            val adventure = adventureSnapshot.toObject(Adventure::class.java)
            if (adventure != null) {
                emit(Resource.Success(adventure))
            } else {
                //(Resource.Failure(
            }
        } catch (e: Exception) {
            //emit(Resource.Failure(e.message ?: "An error occurred"))
        }
    }

    override suspend fun addCommentToAdventure(uid: String, adventureId: String, comment: Comment) {
        try {
            databaseService.addCommentToAdventure(adventureId , comment)
            databaseService.updateUserPoints(uid, 5) //5 poena korisnik dobija dodavanjem komentara
        } catch (e: Exception) {
            // Handle exception
            throw e
        }
    }
    override suspend fun getCommentsForAdventures(adventureId: String): List<Comment> {
        return try {
            val bookDocument = firestoreInstance.collection("adventures").document(adventureId).get().await()
            val comments = bookDocument.toObject(Adventure::class.java)?.comments ?: emptyList()
            comments
        } catch (e: Exception) {
            Log.e("AdventureRepositoryImpl", "Error fetching comments for book", e)
            emptyList()
        }
    }


    override suspend fun markAdventureAsVisited(adventureId: String, userId: String, level: String) {
        try {
            val adventureRef = firestoreInstance.collection("adventures").document(adventureId)
            val adventure = adventureRef.get().await()
            val updatedVisitList = adventure.get("visitedUsers") as? List<String> ?: listOf()

            if (userId !in updatedVisitList) {
                adventureRef.update("visitedUsers", updatedVisitList + userId).await()
            }
            databaseService.markAdventureAsVisited(adventureId, userId)
            databaseService.updateUserPoints(userId, 0, level)

        } catch (e: Exception) {
            // Handle exception
            throw e
        }

    }

}