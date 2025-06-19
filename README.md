# 🧠 Sobriety Test App 🍻🚫

A modern Android application built with **Jetpack Compose** that helps assess sobriety using interactive tests measuring **reaction**, **memory**, and **balance**. This app uses **Room** for persistent score tracking and follows **MVVM architecture** with **state management** via `ViewModel`. [Link to Notes & Resource Sheet](https://kingjulien1.notion.site/Final-Project-Sobriety-Tester-1ed86e6823a480aaa6c8dbe07d29b480?pvs=74)

---

## 📱 Features

✅ Three mini-tests to assess cognitive and motor skills:  
- 🎯 **Reaction Test**: Tap the dots as fast as you can!  
- 🧩 **Memory Test**: Watch and remember sequences of dots.  
- ⚖️ **Balance Test**: Keep the dot level for as long as possible.

✅ 🧮 Score tracking after each test  
✅ 🕒 3-second countdown before each test begins  
✅ 🏆 Final score summary with percentage-based performance  
✅ 💾 Persistent storage with Room database  
✅ 📦 Clean architecture with MVVM and Compose state separation

---


## 👥 Collaboration & Task Split

This project is a team effort, with responsibilities divided as follows:

### 👨 Julian's Responsibilities
- 🔧 App setup and project scaffolding
- 🎯 Implementing the **Reaction Test**
- 🧠 Implementing the **Cognitive (Memory) Test**

### 👩 Lea's Responsibilities
- ⚖️ Implementing the **Balance Test** (requires device sensor testing)
- 📊 Final Result screen and score percentage logic
- 🧪 On-device testing and sensor calibration

I don't have an android device, which is why Lea will be doing any sensor based functionality.

---


## 📐 Architecture Overview

```
MainActivity
   └── Jetpack Compose UI
        ├── Navigation (NavHost)
        ├── Screens (Reaction, Memory, Balance, Score, Result)
        └── AppViewModel (shared ViewModel)

AppViewModel
   └── Business logic and score management
        └── Room Database (ScoreDao, Score Entity)
```

### Layers

- **UI Layer**: Built with Jetpack Compose and Material 3
- **ViewModel**: Handles state, side-effects, and DB interaction
- **Data Layer**: Room database stores all test scores

---

## 🏗️ Implementation Details

### 🔄 Navigation

Navigation is handled using `androidx.navigation:navigation-compose`. The app has the following routes:
- `reaction_test`
- `memory_test`
- `balance_test`
- `score_screen`
- `final_result`

All screens are registered in the `NavHost` via `NavController`.

### 🧠 State Management

The `AppViewModel` exposes:
```kotlin
val totalScore: StateFlow<Int>
```

To track cumulative points, and uses:
```kotlin
fun addScore(points: Int)
```

For recording test results using Room.

### 💽 Room Database

- **Entity**: `Score`
- **DAO**: `ScoreDao`
- **Database**: `AppDatabase`

Room is configured as a singleton database and is accessed via `getDatabase(context)`.

---

## 📦 Dependencies

Be sure to include the following in your `build.gradle.kts`:

```kotlin
// Compose
implementation("androidx.compose.material3:material3:<version>")

// Navigation for Compose
implementation("androidx.navigation:navigation-compose:2.7.7")

// Room
implementation("androidx.room:room-runtime:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:<version>")
```

---

## 🛠️ To-Do

- [ ] Implement each test UI and logic
- [ ] Add countdown timer before each test
- [ ] Add transition animations
- [ ] Polish UI with custom design
- [ ] Unit tests for ViewModel and DAO

---

## 🧾 License

This project is licensed under the [MIT License](LICENSE).

---

## ✨ Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Android Room](https://developer.android.com/training/data-storage/room)
- [Material Design 3](https://m3.material.io/)
