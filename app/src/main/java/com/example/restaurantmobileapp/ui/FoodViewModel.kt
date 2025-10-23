package com.example.restaurantmobileapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantmobileapp.data.FoodDatabase
import com.example.restaurantmobileapp.data.FoodEntity
import com.example.restaurantmobileapp.data.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FoodRepository(FoodDatabase.getDatabase(application).foodDao())

    val categories = repository.getAllFoods()
        .map { foods ->
            foods.groupBy { it.categoryLevel1 }.map { (category, items) ->
                CategoryUiModel(
                    name = category,
                    imageUrl = items.flatMap { it.imageUrls }.firstOrNull().orEmpty(),
                    hasSubcategories = items.any { !it.categoryLevel2.isNullOrBlank() }
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun subcategories(category: String): Flow<List<SubcategoryUiModel>> =
        repository.getFoodsByCategory(category).map { foods ->
            foods.filter { !it.categoryLevel2.isNullOrBlank() }
                .groupBy { it.categoryLevel2 }
                .map { (subcategory, items) ->
                    SubcategoryUiModel(
                        category = category,
                        name = subcategory ?: "",
                        imageUrl = items.flatMap { it.imageUrls }.firstOrNull().orEmpty()
                    )
                }
        }

    fun foods(category: String, subcategory: String?): Flow<List<FoodListItemUiModel>> {
        return if (subcategory.isNullOrEmpty()) {
            repository.getFoodsByCategory(category)
        } else {
            repository.getFoodsBySubcategory(category, subcategory)
        }.map { foods ->
            foods.map { it.toListUiModel() }
        }
    }

    fun foodDetail(id: Int): Flow<FoodDetailUiModel> = repository.getFoodById(id).map { it.toDetailUiModel() }

    private fun FoodEntity.toListUiModel() = FoodListItemUiModel(
        id = id,
        name = name,
        imageUrl = imageUrls.firstOrNull().orEmpty()
    )

    private fun FoodEntity.toDetailUiModel() = FoodDetailUiModel(
        id = id,
        name = name,
        description = description,
        ingredients = ingredients,
        imageUrls = imageUrls,
        videoUrl = videoUrl
    )
}

data class CategoryUiModel(
    val name: String,
    val imageUrl: String,
    val hasSubcategories: Boolean
)

data class SubcategoryUiModel(
    val category: String,
    val name: String,
    val imageUrl: String
)

data class FoodListItemUiModel(
    val id: Int,
    val name: String,
    val imageUrl: String
)

data class FoodDetailUiModel(
    val id: Int,
    val name: String,
    val description: String,
    val ingredients: String,
    val imageUrls: List<String>,
    val videoUrl: String?
)
