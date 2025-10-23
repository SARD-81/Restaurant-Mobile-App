package com.example.restaurantmobileapp.data

import kotlinx.coroutines.flow.Flow

class FoodRepository(private val dao: FoodDao) {
    fun getAllFoods(): Flow<List<FoodEntity>> = dao.getFoods()

    fun getCategories(): Flow<List<String>> = dao.getCategories()

    fun getSubcategories(category: String): Flow<List<String>> = dao.getSubcategories(category)

    fun getFoodsByCategory(category: String): Flow<List<FoodEntity>> = dao.getFoodsByCategory(category)

    fun getFoodsBySubcategory(category: String, subcategory: String): Flow<List<FoodEntity>> =
        dao.getFoodsBySubcategory(category, subcategory)

    fun getFoodById(id: Int): Flow<FoodEntity> = dao.getFoodById(id)
}
