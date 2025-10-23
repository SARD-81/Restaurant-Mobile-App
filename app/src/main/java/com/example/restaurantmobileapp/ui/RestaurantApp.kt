package com.example.restaurantmobileapp.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage

@Composable
fun RestaurantApp(viewModel: FoodViewModel) {
    val navController = rememberNavController()
    val currentDestination by navController.currentBackStackEntryAsState()
    val currentRoute = currentDestination?.destination?.route ?: Destinations.Categories

    Scaffold(
        topBar = {
            RestaurantTopBar(
                currentRoute = currentRoute,
                canNavigateBack = navController.previousBackStackEntry != null,
                onNavigateBack = { navController.popBackStack() },
                onAboutClick = { navController.navigate(Destinations.About) },
                onAddressClick = { navController.navigate(Destinations.Address) }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.Categories,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destinations.Categories) {
                val categories by viewModel.categories.collectAsStateWithLifecycle()
                CategoryGrid(
                    items = categories,
                    onCategoryClick = { category ->
                        if (category.hasSubcategories) {
                            navController.navigate(Destinations.subcategoriesRoute(category.name))
                        } else {
                            navController.navigate(
                                Destinations.foodListRoute(
                                    category = category.name,
                                    subcategory = null
                                )
                            )
                        }
                    }
                )
            }

            composable(
                route = Destinations.Subcategories,
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category")?.let { Uri.decode(it) } ?: ""
                val subcategories by viewModel.subcategories(category).collectAsState(initial = emptyList())
                var hasLoaded by remember(category) { mutableStateOf(false) }
                var hasNavigated by remember(category) { mutableStateOf(false) }

                LaunchedEffect(subcategories) {
                    hasLoaded = true
                }

                when {
                    !hasLoaded -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    subcategories.isEmpty() && !hasNavigated -> {
                        LaunchedEffect(category) {
                            hasNavigated = true
                            navController.popBackStack()
                            navController.navigate(Destinations.foodListRoute(category, null))
                        }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    subcategories.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    else -> {
                        SubcategoryGrid(
                            title = category,
                            items = subcategories,
                            onSubcategoryClick = { subcategory ->
                                navController.navigate(
                                    Destinations.foodListRoute(
                                        category = category,
                                        subcategory = subcategory
                                    )
                                )
                            }
                        )
                    }
                }
            }

            composable(
                route = Destinations.FoodList,
                arguments = listOf(
                    navArgument("category") { type = NavType.StringType },
                    navArgument("subcategory") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category")?.let { Uri.decode(it) } ?: ""
                val subcategoryArgument = backStackEntry.arguments?.getString("subcategory")
                val subcategory = subcategoryArgument?.takeIf { it != Destinations.EMPTY_SUBCATEGORY }?.let { Uri.decode(it) }
                val foods by viewModel.foods(category, subcategory).collectAsState(initial = emptyList())
                FoodListScreen(
                    title = subcategory ?: category,
                    items = foods,
                    onFoodClick = { foodId ->
                        navController.navigate(Destinations.foodDetailRoute(foodId))
                    }
                )
            }

            composable(
                route = Destinations.FoodDetail,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                val foodDetail by viewModel.foodDetail(id).collectAsState(initial = null)
                foodDetail?.let {
                    FoodDetailScreen(food = it)
                }
            }

            composable(Destinations.About) {
                AboutScreen()
            }

            composable(Destinations.Address) {
                AddressScreen()
            }
        }
    }
}

private object Destinations {
    const val Categories = "categories"
    const val Subcategories = "subcategories/{category}"
    const val EMPTY_SUBCATEGORY = "-"
    const val FoodList = "food_list/{category}/{subcategory}"
    const val FoodDetail = "food_detail/{id}"
    const val About = "about"
    const val Address = "address"

    fun subcategoriesRoute(category: String): String =
        "subcategories/${Uri.encode(category)}"

    fun foodListRoute(category: String, subcategory: String?): String =
        "food_list/${Uri.encode(category)}/${Uri.encode(subcategory ?: EMPTY_SUBCATEGORY)}"

    fun foodDetailRoute(id: Int): String = "food_detail/$id"
}

@Composable
private fun RestaurantTopBar(
    currentRoute: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    onAboutClick: () -> Unit,
    onAddressClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = when {
                    currentRoute.startsWith("food_detail") -> "جزئیات غذا"
                    currentRoute.startsWith("food_list") -> "منو"
                    currentRoute.startsWith("subcategories") -> "دسته‌بندی"
                    currentRoute == Destinations.About -> "درباره رستوران"
                    currentRoute == Destinations.Address -> "آدرس رستوران"
                    else -> "منوی اصلی"
                }
            )
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            }
        },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(text = "درباره رستوران") },
                    onClick = {
                        expanded = false
                        onAboutClick()
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null)
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = "آدرس رستوران") },
                    onClick = {
                        expanded = false
                        onAddressClick()
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                    }
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun CategoryGrid(
    items: List<CategoryUiModel>,
    onCategoryClick: (CategoryUiModel) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
private fun CategoryCard(
    category: CategoryUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = category.imageUrl,
                contentDescription = category.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (category.hasSubcategories) "دارای زیر دسته" else "بدون زیر دسته",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SubcategoryGrid(
    title: String,
    items: List<SubcategoryUiModel>,
    onSubcategoryClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold
        )
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(160.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { subcategory ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clickable { onSubcategoryClick(subcategory.name) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = subcategory.imageUrl,
                            contentDescription = subcategory.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = subcategory.name,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodListScreen(
    title: String,
    items: List<FoodListItemUiModel>,
    onFoodClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { food ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFoodClick(food.id) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column {
                        AsyncImage(
                            model = food.imageUrl,
                            contentDescription = food.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = food.name,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodDetailScreen(food: FoodDetailUiModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MediaCarousel(images = food.imageUrls, videoUrl = food.videoUrl)
        Text(
            text = food.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "توضیحات",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(text = food.description, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "مواد اولیه",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(text = food.ingredients, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MediaCarousel(images: List<String>, videoUrl: String?) {
    val pages = images + listOfNotNull(videoUrl)
    androidx.compose.foundation.pager.HorizontalPager(
        pageCount = pages.size,
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) { page ->
        val item = pages[page]
        if (page < images.size) {
            AsyncImage(
                model = item,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            VideoPlayer(url = item)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun AboutScreen() {
    val htmlContent = """
        <html>
        <body style='padding:16px;font-family: sans-serif;'>
        <h2>درباره رستوران</h2>
        <p>رستوران ما با الهام از فرهنگ غذایی ایران و جهان، مجموعه‌ای از غذاهای با کیفیت را ارائه می‌دهد.</p>
        <p>تمامی مواد اولیه تازه و روزانه تهیه می‌شوند و تیم سرآشپز ما تلاش می‌کند تجربه‌ای به یادماندنی را برای شما رقم بزند.</p>
        <p>ساعات کاری: هر روز از ساعت ۱۱ صبح تا ۱۲ شب.</p>
        </body>
        </html>
    """
    WebViewContainer(content = htmlContent, isHtml = true)
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun AddressScreen() {
    val addressUrl = "https://www.google.com/maps/search/?api=1&query=%D8%B1%D8%B3%D8%AA%D9%88%D8%B1%D8%A7%D9%86+%D8%AF%D8%B1+%D8%AA%D9%87%D8%B1%D8%A7%D9%86"
    WebViewContainer(content = addressUrl, isHtml = false)
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewContainer(content: String, isHtml: Boolean) {
    AndroidViewWrapper { webView ->
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        if (isHtml) {
            webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null)
        } else {
            webView.loadUrl(content)
        }
    }
}

@Composable
private fun AndroidViewWrapper(configure: (WebView) -> Unit) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context -> WebView(context).apply { configure(this) } },
        update = { configure(it) }
    )
}

@Composable
private fun VideoPlayer(url: String) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            android.widget.VideoView(context).apply {
                setVideoURI(Uri.parse(url))
                setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.isLooping = true
                    start()
                }
            }
        },
        update = { view ->
            if (!view.isPlaying) {
                view.start()
            }
        }
    )
}
