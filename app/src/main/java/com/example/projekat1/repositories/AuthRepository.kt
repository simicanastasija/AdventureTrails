package com.example.projekat1.repositories

import android.net.Uri
import com.example.projekat1.models.User
import com.example.projekat1.services.DbService
import com.example.projekat1.services.StorageService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class AuthRepository : IAuthRepository
{
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val databaseService = DbService(firestore)
    private val storageService = StorageService(storage)

    override val user: FirebaseUser? get() = firebaseAuth.currentUser

    override suspend fun logIn(email: String, password: String) : Resource<FirebaseUser>
    {
        return try
        {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        }
        catch(e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        profileImg: Uri
    ) : Resource<FirebaseUser>
    {
        return try
        {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            if(result.user != null)
            {
                val profilePictureUrl = storageService.uploadUserPfp(result.user!!.uid, profileImg)

                val user = User (
                    email = email,
                    password = password,
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    profileImg = profilePictureUrl
                )

                databaseService.saveUserData(result.user!!.uid, user)
            }

            Resource.Success(result.user!!)
        }
        catch(e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override fun logOut()
    {
        firebaseAuth.signOut()
    }

    override suspend fun getUser(): Resource<User>
    {
        return try
        {
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null)
            {
                val uid = currentUser.uid

                val db = FirebaseFirestore.getInstance()

                val userDocRef = db.collection("users").document(uid)
                val userSnapshot = userDocRef.get().await()

                if (userSnapshot.exists())
                {
                    val customUser = userSnapshot.toObject(User::class.java)

                    if (customUser != null)
                    {
                        Resource.Success(customUser)
                    }
                    else {
                        Resource.Failure(Exception("[ERROR] Failed to map snapshot document to User! (User ID: ${uid})"))
                    }
                }
                else {
                    Resource.Failure(Exception("[ERROR] User snapshot document does not exist! (User ID: ${uid})"))
                }
            }
            else {
                Resource.Failure(Exception("[ERROR] No current user session found!"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getAllUsers(): Resource<List<User>>
    {
        return try
        {
            val db = FirebaseFirestore.getInstance()
            val usersCollectionRef = db.collection("users")
            val usersSnapshot = usersCollectionRef.get().await()

            if (!usersSnapshot.isEmpty)
            {
                val usersList = usersSnapshot.documents.mapNotNull { document ->
                    document.toObject(User::class.java)
                }
                Resource.Success(usersList)
            }
            else {
                Resource.Failure(Exception("[INFO] No users found in the database."))
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

}