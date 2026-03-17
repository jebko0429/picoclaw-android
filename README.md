# PicoClaw Android (Option 1: native Go backend + Android wrapper)

This scaffold wires a foreground Android service to host the PicoClaw Go backend via `gomobile bind`. Kotlin handles Android permissions/notifications; Go stays focused on the assistant logic.

## What’s here
- Android app module (`app/`) with:
  - Foreground service `PicoService` (keeps process alive).
  - `MainActivity` start/stop buttons and notification permission request.
  - WebView launcher (`WebViewActivity`) loading `app/src/main/assets/index.html` (replace with your built UI).
  - Boot receiver to auto-start after reboot; WorkManager periodic keep-alive.
  - Manifest permissions for network/Wi‑Fi/foreground service/notifications.
- Gradle setup (compileSdk/targetSdk 34, Kotlin 1.9.25).
- Network security config allowing localhost cleartext for local HTTP, if you expose one.

## What you still need to do
1) **Install toolchains** (on a development machine, not this device):
   - JDK 17, Android SDK/NDK, Android Build Tools 34, Go 1.21+.
   - gomobile:  
     ```bash
     go install golang.org/x/mobile/cmd/gomobile@latest
     gomobile init
     ```
2) **Bind PicoClaw Go code to Android** (from repo root):
   ```bash
   cd picoclaw
   # Pick the package that starts the server; adjust import path if different.
   gomobile bind -target=android/arm64 -o ../picoclaw-android/app/libs/picoclaw.aar ./cmd/picoclaw
   ```
   - If you need multiple ABIs, add `arm`, `386`, `amd64`, but expect larger APK.
3) **Hook the service to the Go entrypoints**:
   - In `PicoService.kt`, replace the `TODO` lines with calls from the generated AAR, e.g.:
     ```kotlin
     import io.picoclaw.Picoclaw // package name from gomobile
     ...
     Picoclaw.start(baseContext.filesDir.absolutePath) // start your server
     ...
     Picoclaw.stop()
     ```
   - Ensure the Go package exports functions with uppercase names for gomobile (e.g., `func Start(dataDir string)`).
4) **Frontend (optional)**:
   - Replace `app/src/main/assets/index.html` with your built web UI, or edit `WebViewActivity` to point to `http://127.0.0.1:<port>` served by the backend.
5) **Add icons**: provide `mipmap/ic_launcher` assets or let Android Studio generate them.
6) **Build & run**:
   ```bash
   cd picoclaw-android
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```
7) **Background survival**:
   - Uses `START_STICKY`, boot receiver (BOOT_COMPLETED), WorkManager periodic keep-alive, and battery-optimization whitelist prompt.
   - Foreground notification is required; channel is LOW importance by default.
8) **Permissions**:
   - Runtime: POST_NOTIFICATIONS (API 33+). Request in `MainActivity`.
   - Wi‑Fi toggles require `CHANGE_WIFI_STATE` (already declared); on Android 13+ may need `NEARBY_WIFI_DEVICES` for certain operations.
   - Mobile data toggling generally not allowed for third‑party apps; you can read state via `ConnectivityManager`.

## Building on-device with Android Code Studio (mobile)
- Android Code Studio typically lacks Go/gomobile. Build the AAR on a desktop, copy it into `app/libs/picoclaw.aar`, and uncomment the dependency in `app/build.gradle.kts`.
- Ensure JDK 17 and Android SDK/NDK/Build Tools 34 are available in the app; if not, sideload those folders.
- The project already declares all needed permissions and keep-alive helpers. Replace `app/src/main/assets/index.html` with your UI or point `WebViewActivity` to your local backend URL.

## Notes for this environment
- This device lacks Go and full Android toolchains; perform `gomobile bind` and `gradlew` on a development machine (Linux/macOS/Windows with Android SDK).
- `/sdcard` write is blocked for this app; keep assets inside app internal storage or download at runtime.

## Next steps
- Confirm the Go entrypoints to expose (start/stop, config paths, logging).
- Decide if you need a WebView UI; if yes, load `file:///android_asset/www/index.html` and proxy to the backend at `http://127.0.0.1:<port>`.
- Add crash/health logging (e.g., Logcat + backend logs in `filesDir`). 
