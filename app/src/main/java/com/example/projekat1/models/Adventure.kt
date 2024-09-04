package com.example.projekat1.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class Adventure (
    @DocumentId var id: String = "",
    val userId: String= "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val title: String= "",
    val description: String= "",
    val type: String= "",
    val level: String= "", // nivo tezine kao ispuniti avanturu lako, umereno i tesko
    val adventureImages: List<String> = emptyList(),
    val isVisited: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val visitedUsers: List<String> = emptyList()
)

data class Comment(
    val userId: String = "",
    val userName: String = "",
    val timestamp: Long = 0L,
    val text: String = ""
)

