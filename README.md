# OkDriver Panic Button

A modern, responsive Android panic button application built with Kotlin and Jetpack Compose. The app allows drivers to quickly trigger an SOS alert that automatically contacts the nearest available registered members in case of an emergency.






## Approach & Architecture

- **UI Framework:** Built entirely using **Jetpack Compose** for a reactive, state-driven, and smooth user interface. Features custom animations (e.g., pulsing search indicators, countdown rings, glassmorphism cards).
- **Architecture:** Follows the **MVVM (Model-View-ViewModel)** pattern. The UI observes a single source of truth (`PanicState`) emitted via `StateFlow` from the `PanicViewModel`. 
- **Mock Dispatch System:** The panic flow simulates locating nearby members using Euclidean distance, adding them to a priority queue, and sequentially requesting help with simulated network delays and auto-timeouts.
- **Local Storage:** Utilizes `SharedPreferences` to persistently store emergency contacts, global settings (like SOS message and timer duration), and incident history logs.
- **Navigation:** Handled via **Navigation Compose**, with a sealed `Screen` class defining type-safe routes and smooth transition animations between screens (Splash, Main SOS, Menu, Contacts, History).

## Libraries Used

- **Jetpack Compose** (`ui`, `material3`, `animation`) - For building the declarative native UI.
- **Compose Navigation** (`androidx.navigation:navigation-compose`) - For in-app routing.
- **Kotlin Coroutines & Flow** (`kotlinx.coroutines`) - For asynchronous operations, countdown timers, and reactive state management.
- **AndroidX Core & Lifecycle** (`androidx.core:core-ktx`, `androidx.lifecycle:lifecycle-runtime-ktx`) - Core Android components.

## How to Run the Project

1. **Prerequisites:** Ensure you have [Android Studio](https://developer.android.com/studio) installed.
2. **Open the Project:** Launch Android Studio, select **File > Open**, and choose the root directory of this repository (`OkDriverPanicButton`).
3. **Sync Gradle:** Wait for Gradle to automatically sync and download the required dependencies.
4. **Run the App:** 
   - Connect a physical Android device or start an Android Emulator.
   - Click the **Run** button (green play icon) in the top toolbar or press `Shift + F10`.

## Features
- Interactive SOS button with haptic feedback and a safety countdown.
- Simulated emergency dispatch connecting to mock nearby users.
- Contact registry to manage trusted members.
- History log of previous panic events.
