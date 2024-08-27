package com.example.projekat1.repositories

import android.net.Uri
import com.example.projekat1.models.User
import com.google.firebase.auth.FirebaseUser

interface IAuthRepository
{
    val user: FirebaseUser?

    suspend fun logIn(email : String, password : String) : Resource<FirebaseUser>
    suspend fun register(email : String, password: String, fullName : String, phoneNumber : String, profileImg : Uri) : Resource<FirebaseUser>
    fun logOut()

    suspend fun getUser(): Resource<User>
    suspend fun getAllUsers(): Resource<List<User>>
}