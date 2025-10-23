package com.example.restaurantmobileapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food")
data class FoodEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val categoryLevel1: String,
    val categoryLevel2: String?,
    val description: String,
    val ingredients: String,
    val imageUrls: List<String>,
    val videoUrl: String?
)
