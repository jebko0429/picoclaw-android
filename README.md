# PicoClaw Android (Option 1: native Go backend + Android wrapper)

This variant targets **remote-backend** mode so you can build entirely inside Android Code Studio (no gomobile). The app is a Kotlin wrapper with a foreground service, WebView UI, and remote backend URL config.

## What’s here
- Android app module (`app/`) with:
  - Foreground service `PicoService` to keep the client alive.
  - `MainActivity` start/stop buttons and notification permission request.
  - WebView launcher (`WebViewActivity`) that targets `http://<backendHost>:<backendPort>/` and falls back to the bundled asset.
  - Boot receiver + WorkManager keep-alive.
  - Permissions for network/Wi‑Fi/foreground service/notifications.
  - Notification icon vector at `app/src/main/res/drawable/ic_stat_name.xml`.
- Gradle setup (compileSdk/targetSdk 34, Kotlin 1.9.25).
- Network security config allowing cleartext (needed if your backend is http://).

## What you still need to do
1) **Set your backend URL**: Edit `PicoConfig.backendHost` / `backendPort` (Kotlin object) to point to your remote PicoClaw server.
2) **Frontend (optional)**:
   - Replace `app/src/main/assets/index.html` with your built web UI, or keep the fallback text.
3) **Add icons**: provide `mipmap/ic_launcher` assets or let Android Studio generate them.
4) **Build & run**:
   ```bash
   cd picoclaw-android
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```
5) **Background survival**:
   - Uses `START_STICKY`, boot receiver (BOOT_COMPLETED), WorkManager periodic keep-alive, and battery-optimization whitelist prompt.
   - Foreground notification is required; channel is LOW importance by default.
6) **Permissions**:
   - Runtime: POST_NOTIFICATIONS (API 33+). Request in `MainActivity`.
   - Wi‑Fi toggles require `CHANGE_WIFI_STATE` (already declared); on Android 13+ may need `NEARBY_WIFI_DEVICES` for certain operations.
   - Mobile data toggling generally not allowed for third‑party apps; you can read state via `ConnectivityManager`.

## Building on-device with Android Code Studio (mobile)
- No desktop required for this remote-backend variant. Android Code Studio’s built-in JDK/Gradle/SDK are enough.
- Just set the backend host in `PicoConfig`, replace the asset UI if desired, and build.

## Notes for this environment
- This device lacks Go and full Android toolchains; perform `gomobile bind` and `gradlew` on a development machine (Linux/macOS/Windows with Android SDK).
- `/sdcard` write is blocked for this app; keep assets inside app internal storage or download at runtime.

## Next steps
- Confirm the Go entrypoints to expose (start/stop, config paths, logging).
- Decide if you need a WebView UI; if yes, load `file:///android_asset/www/index.html` and proxy to the backend at `http://127.0.0.1:<port>`.
- Add crash/health logging (e.g., Logcat + backend logs in `filesDir`). 
