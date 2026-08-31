# ListenIn

A native Android media browser built with Jetpack Compose. ListenIn scans the device for audio and video files via `MediaStore` and presents them in a clean, dark-themed interface.

## Features

- **Splash screen** with an animated headphones logo and runtime media-permission handling.
- **Landing screen** to choose between browsing **Audio** or **Video** libraries.
- **Media list** that loads items from the device's `MediaStore`, showing title, subtitle (artist for audio, resolution for video), duration, size, and thumbnail.
- **Runtime permissions** for `READ_MEDIA_AUDIO` / `READ_MEDIA_VIDEO` (Android 13+) with a legacy `READ_EXTERNAL_STORAGE` fallback (≤ Android 12).
- **Edge-to-edge** UI with a consistent black theme.

## Tech stack

| Area | Choice |
|------|--------|
| Language | Kotlin |
| UI toolkit | Jetpack Compose (Material 3) |
| Navigation | Navigation Compose 2.9.0 |
| Async | Kotlin Coroutines |
| Media source | Android `MediaStore` (ContentResolver) |
| Min / Target / Compile SDK | 26 / 36 / 37 |

## Project structure

```
app/src/main/java/com/example/listenin/
├── MainActivity.kt              # Entry point, edge-to-edge + theme + NavHost
├── data/
│   ├── MediaItem.kt             # MediaItem model + MediaType enum
│   └── MediaRepository.kt       # Queries MediaStore for audio/video (IO dispatcher)
└── ui/
    ├── navigation/AppNavHost.kt # Routes: Splash → Landing → MediaList
    ├── splash/SplashScreen.kt   # Animated splash + permission request
    ├── landing/LandingScreen.kt # Audio / Video entry points
    ├── medialist/MediaListScreen.kt
    ├── common/                  # Reusable UI (GenericScaffold, icons, thumbnails, padding)
    └── theme/                   # Colors, typography, Material theme
```

Navigation flow:

```
Splash ──(permission granted)──► Landing ──► MediaList (audio | video)
```

## Getting started

### Prerequisites

- Android Studio (recent version) or the Android SDK command-line tools
- A JDK that includes `jlink` — the **JDK bundled with Android Studio** (JBR) works out of the box
- An Android device (API 26+) or emulator

> **Note on the JDK:** The build requires a full JDK. If Gradle picks up a cut-down JRE (e.g. from an IDE extension) you'll see a `jlink executable ... does not exist` error. Point Gradle at Android Studio's bundled JDK, for example in `~/.gradle/gradle.properties`:
> ```
> org.gradle.java.home=/Applications/Android Studio.app/Contents/jbr/Contents/Home
> ```

### Build & run

Clone, then from the project root:

```bash
# Build and install the debug build on a connected device
./gradlew installDebug

# Launch it
adb shell monkey -p com.example.listenin -c android.intent.category.LAUNCHER 1
```

Or simply open the project in Android Studio and press **Run ▶**.

To target a specific device when several are connected:

```bash
adb devices -l                     # find the serial
ANDROID_SERIAL=<serial> ./gradlew installDebug
```

## Permissions

ListenIn reads media the user already has on the device. It requests, at runtime:

- `READ_MEDIA_AUDIO`, `READ_MEDIA_VIDEO` — Android 13 (API 33) and above
- `READ_EXTERNAL_STORAGE` — Android 12 (API 32) and below
