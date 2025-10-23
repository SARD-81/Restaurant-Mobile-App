package com.example.restaurantmobileapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM food")
    fun getFoods(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM food WHERE categoryLevel1 = :category")
    fun getFoodsByCategory(category: String): Flow<List<FoodEntity>>

    @Query("SELECT * FROM food WHERE categoryLevel1 = :category AND categoryLevel2 = :subcategory")
    fun getFoodsBySubcategory(category: String, subcategory: String): Flow<List<FoodEntity>>

    @Query("SELECT * FROM food WHERE id = :id")
    fun getFoodById(id: Int): Flow<FoodEntity>

    @Query("SELECT DISTINCT categoryLevel1 FROM food ORDER BY categoryLevel1")
    fun getCategories(): Flow<List<String>>

    @Query("SELECT DISTINCT categoryLevel2 FROM food WHERE categoryLevel1 = :category AND categoryLevel2 IS NOT NULL AND categoryLevel2 != '' ORDER BY categoryLevel2")
    fun getSubcategories(category: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FoodEntity>)
}
