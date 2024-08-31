package com.example.projekat1.services


import com.example.projekat1.models.Adventure
import com.example.projekat1.models.Comment
import com.example.projekat1.models.User
import com.example.projekat1.repositories.Resource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DbService(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveUserData( id: String, user: User) : Resource<String>
    {
        return try
        {
            firestore.collection("users").document(id).set(user).await()
            Resource.Success("[INFO] User data saved successfully. (User ID: ${id})")
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun getUserData( id: String ) : Resource<String>
    {
        return try
        {
            val userDocRef = firestore.collection("users").document(id)
            val userSnapshot = userDocRef.get().await()

            if(userSnapshot.exists())
            {
                val user = userSnapshot.toObject(User::class.java)
                if(user != null) {
                    Resource.Success(user)
                }
                else {
                    Resource.Failure(Exception("[ERROR] User not found! (User ID: ${id})"))
                }
            }
            else {
                Resource.Failure(Exception("[ERROR] User snapshot not found (User ID: ${id})"))
            }

            Resource.Success("[INFO] Successfully retrieved user data. (User ID: ${id})")

        }
        catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun saveAdventure(
        adventure: Adventure
    ): Resource<String>{
        return try{
            firestore.collection("adventures").add(adventure).await()
            Resource.Success("Podaci o avanturi su uspesno sacuvani")
        }catch(e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun addCommentToAdventure(adventureId: String, comment: Comment) {
        try {
            val adventureRef = firestore.collection("adventures").document(adventureId)
            adventureRef.update("comments", FieldValue.arrayUnion(comment)).await()
        } catch (e: Exception) {
            // Handle exception
            throw e
        }
    }

    suspend fun updateUserPoints(
        uid: String,
        points: Int
    ): Resource<String>{
        return try {
            val userDocRef = firestore.collection("users").document(uid)
            val userSnapshot = userDocRef.get().await()

            if(userSnapshot.exists()){
                val user = userSnapshot.toObject(User::class.java)
                if(user != null){
                    val newPoints = user.totalPoints + points
                    userDocRef.update("totalPoints", newPoints).await()
                    Resource.Success("Uspesno azurirani poeni korisnika!")
                } else {
                    Resource.Failure(Exception("Korisnik ne postoji"))
                }
            } else {
                Resource.Failure(Exception("Korisnikov dokument ne postoji"))
            }
            Resource.Success("Uspesno dodati podaci o korisniku")
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }


}