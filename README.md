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

## 🎨 Design References & Similar Apps

Here are some UI/UX inspirations and related applications that guided our design and implementation:

- [Google Fit](https://play.google.com/store/apps/details?id=com.google.android.apps.fitness): Clean UI and real-time sensor integration.  
- [Reflex Test Apps](https://play.google.com/store/search?q=reflex+test&c=apps): Simple reaction-based games for benchmarking.  
- [Brain Training Apps (e.g., Lumosity)](https://www.lumosity.com/): For cognitive and memory test inspiration.  
- [Balance Training Tools](https://play.google.com/store/search?q=balance+test+sensor&c=apps): Sensor-based exercises using the accelerometer.
- [Reaction Test Pro](https://apps.apple.com/at/app/reaction-test-pro/id493360516?l=en-GB): A reaction test using different methods of reaction speed
- [Beam Highlights](https://www.ridebeam.com/highlight/beam-introduces-rider-check-a-cognitive-test-for-enhanced-safety): A set of cognitive tests, each testing different cognition abilities, prettily designed
- [Dribble Collection](https://dribbble.com/kingjulien1/collections/7497983-sobriety-tester) a small collection of design references & ideas for our tests & app components

These references helped shape the interface, game flow, and scoring system with mobile ergonomics and clarity in mind.

---

## 💡 Miscellaneous Notes & Ideas

- Tests are modular and can be expanded (e.g., add visual illusions, coordination games).  
- Sound feedback per reaction or countdown tick would enhance immersion.  
- Average performance across users could be compared if analytics were added.  
- Could offer a "Night Mode" for dim environments.  
- Long-term idea: a calibration test baseline to personalize scoring per user.
- show result as points or as percentage?
- show comparison to average test results - “You are better than 84% of testers”
- show prompt depending on points - “You are (not) safe to drive” …
- share count down components between tests
- share same design of dots between tests
- animate from count down into testing dot / from testing dot into count down / from testing dot into result points
- show list of previous test results (save to database?)
- Device capabilities (e.g., missing gyroscope) may influence test availability.  

These thoughts are a mix of possible features and design considerations that may help extend the app into a broader cognitive tracking or training tool.

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

## 🗓️ Future Dates & Deadlines

Keep track of important project milestones:

- 📢 **Project Pitch Presentation**: May 15, 2025  
- 🚀 **Early Opportunity for Presentation**: June 24, 2025  
- 🛠️ **Final Submission Deadline**: August 31, 2025  

Make sure all implementation, testing, and documentation are finalized ahead of the final due date!

---

## ✨ Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Android Room](https://developer.android.com/training/data-storage/room)
- [Material Design 3](https://m3.material.io/)
