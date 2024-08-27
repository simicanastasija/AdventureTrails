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
    val adventureImages: List<String> = emptyList()
)
