package com.example.restaurantmobileapp.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.takeIf { it.isNotBlank() }?.split("|")?.map { it.trim() } ?: emptyList()
    }

    @TypeConverter
    fun listToString(list: List<String>?): String {
        return list?.joinToString(separator = "|") ?: ""
    }
}
