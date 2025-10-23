# Restaurant Mobile App

A Jetpack Compose Android application written in Kotlin that showcases a restaurant menu with category-based navigation, multimedia food details, an "About" page, and an embedded map for the restaurant address. The data is powered by a local Room database that seeds the menu items on first launch.

## ✨ Features
- **Category driven menu** – browse top-level categories such as "فست فود" and "نوشیدنی" with cover photos.
- **Nested navigation** – dive into subcategories (e.g., پیتزا، ساندویچ) or jump straight to dishes if no subcategory exists.
- **Rich dish details** – each menu item shows a swipeable gallery of photos plus an autoplaying video, along with description and ingredients.
- **Informational pages** – access restaurant information via a WebView and view the address on an embedded Google Map.
- **Offline-first data** – a Room database seeds default menu items (name, categories, description, ingredients, image URLs, video URL).

## 📁 Project structure
```
Restaurant-Mobile-App/
├── app/
│   ├── build.gradle.kts        # Module Gradle configuration (Compose, Room, Coil, etc.)
│   └── src/main/
│       ├── AndroidManifest.xml # Declares MainActivity and INTERNET permission
│       ├── java/com/example/restaurantmobileapp
│       │   ├── MainActivity.kt             # Entry point hosting the Compose app
│       │   ├── data/                       # Room database, DAO, entities, seed data
│       │   └── ui/                         # ViewModel and all composable screens
│       └── res/                            # Themes, icons, and XML configs
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew / gradlew.bat + gradle wrapper
```

## 🧰 Prerequisites
1. **Android Studio Giraffe (or newer)** with the latest **Android Gradle Plugin** and **Android SDK 34** installed.
2. **Android device or emulator** running Android 7.0 (API 24) or higher.
3. Stable internet connection (images and videos are loaded from remote URLs).

## 🚀 Getting started (step-by-step)
1. **Install Android Studio** if you haven't already: [developer.android.com/studio](https://developer.android.com/studio).
2. **Clone or copy** this repository to your machine.
   ```bash
   git clone https://github.com/your-account/Restaurant-Mobile-App.git
   ```
3. **Bootstrap the Gradle wrapper (first-time setup)** so Android Studio/Gradle can build the project:
   - macOS/Linux: `./gradlew -v`
   - Windows: `gradlew.bat -v`
   
   The wrapper script will automatically download the missing `gradle-wrapper.jar` from Gradle's official repository. If your environment blocks network traffic, download it manually from `https://github.com/gradle/gradle/raw/v8.2.1/gradle/wrapper/gradle-wrapper.jar` and place it in `gradle/wrapper/`.
4. **Open Android Studio** and choose **"Open an Existing Project"**. Select the `Restaurant-Mobile-App` directory.
5. When prompted, Android Studio will **synchronize Gradle**. Make sure the project finishes syncing without errors (status bar bottom-right).
6. If requested, install any **missing SDK components** (Android Studio will offer a one-click install).
7. **Run the project**:
   - Connect an Android device with USB debugging enabled *or* start an emulator (AVD Manager ▶️ Create Virtual Device).
   - Press **Run ▶️** (or `Shift+F10`). Choose your target device/emulator when prompted.
8. The app will build and deploy. After the first launch, the Room database seeds default dishes automatically. Browse the categories, open dishes, and check the menu options (About & Address).

## 🧭 How the app works
- **Navigation** is handled by `NavHost` (Compose Navigation). Routes include categories, subcategories, dish lists, details, about, and address screens.
- **Database** is provided by `Room` with a `FoodEntity` table. `DefaultFoodData` seeds the initial menu on database creation.
- **ViewModel** (`FoodViewModel`) exposes Flows that the UI collects using `collectAsStateWithLifecycle` for lifecycle-aware updates.
- **Media** uses [Coil](https://github.com/coil-kt/coil) for images and a simple `VideoView` inside Compose for videos.
- **Web content** uses `WebView` composables for both the about page (HTML string) and the Google Maps address search.

## 🧪 Testing & verification
The project currently includes the default Android test dependencies. You can run unit tests with:
```bash
./gradlew test
```
and instrumentation tests (requires device/emulator) with:
```bash
./gradlew connectedAndroidTest
```

## 📌 Notes
- Remote multimedia assets are courtesy of Unsplash and public sample video streams; ensure network access while using the app.
- To customize menu items, edit `DefaultFoodData` or update the Room database logic.
- For production, consider downloading images/videos locally or using a CDN with caching and adding proper loading states.

Enjoy building upon this restaurant experience! 🍽️
