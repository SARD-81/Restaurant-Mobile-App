package com.example.restaurantmobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.restaurantmobileapp.ui.FoodViewModel
import com.example.restaurantmobileapp.ui.RestaurantApp
import com.example.restaurantmobileapp.ui.theme.RestaurantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RestaurantTheme {
                val foodViewModel: FoodViewModel = viewModel(factory = viewModelFactory {
                    initializer { FoodViewModel(application) }
                })
                RestaurantApp(viewModel = foodViewModel)
            }
        }
    }
}
