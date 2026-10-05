apkk download link:https://www.upload-apk.com/jXAIGRCMOVLCrNM
 # Telemedicine Compose Prototype

A Jetpack Compose single-activity Android prototype demonstrating a multilingual telemedicine experience for three roles: Patient, Doctor, and Jan Aushadi Worker. The UI is wired end-to-end with mock data, voice-to-text, and a pluggable symptom prediction client so the app can be demoed without any backend services.

## Highlights
- **Role-based login**: pick Patient, Doctor, or Jan Aushadi Worker to explore contextual dashboards.
- **Global language toggle**: every screen switches between English and Hindi instantly, including top app-bar controls.
- **Patient tools**: health record snapshots, text/voice symptom checker with mock AI predictions, pharmacy stock, and the shared video-call surface.
- **Doctor workflow**: daily schedule, live call queue with accept/reject state, and quick dial room that launches the dummy video UI.
- **Jan Aushadi worker tools**: manage medicine orders, progress in-memory status, and edit inventory counts.
- **Voice first symptom flow**: Hindi speech → transcription → translation → mock API request, with graceful error handling and permission prompts.
- **Development screenshot loader**: optional card that reads customer reference screenshots from external storage during demos.

## Run Instructions
1. Open the project in **Android Studio Ladybug or newer**.
2. Let Gradle sync and run `app` on an emulator or Android device (API 24+).
3. Grant microphone and photo-library permissions when prompted. Voice input and screenshot previews depend on them.

### Matching customer screenshots during a demo
1. Copy the saved UI screenshots from the development machine to the emulator/device path:
   ```bash
   adb push "C:\Users\Kathansh Jain\Saved Games\*" /sdcard/Pictures/SavedGames/
   ```
2. In the Patient overview screen tap **Load demo screenshots**; the app will display any PNG/JPG/WEBP files it finds in `/sdcard/Pictures/SavedGames/`.

## Configuring API keys (optional)
Mock implementations ship enabled by default. To swap in real services, add keys to `local.properties` or pass them as Gradle properties:
```properties
SYMPTOM_PREDICTOR_API_KEY=your-live-key
TRANSLATION_API_KEY=your-translate-key
```
- `BuildConfig.SYMPTOM_PREDICTOR_BASE_URL` currently targets `https://api.symptom-predictor.example/`. Update `app/build.gradle` if your backend uses a different hostname.
- The `SymptomPredictorRepository` uses an OkHttp interceptor that returns canned data. Remove the interceptor and provide a real Retrofit implementation when integrating.
- Translation currently relies on ML Kit on-device models; if you wire a REST service instead, read the key via `BuildConfig.TRANSLATION_API_KEY` inside `TranslatorManager`.

## Permissions requested
- `RECORD_AUDIO` – required for Hindi voice capture in the symptom checker.
- `READ_MEDIA_IMAGES` / `READ_EXTERNAL_STORAGE` – debug-only feature for loading reference screenshots.
- `INTERNET` – needed for Retrofit/OkHttp even though responses are mocked locally.

## Project structure (high level)
- `MainActivity` hosts the navigation graph and global language state.
- `ui/screens` contains Compose screens per role.
- `network` hosts the Retrofit models, service interface, and mock repository.
- `voice` and `translations` wrap the SpeechRecognizer and ML Kit translator.
- `data/MockData.kt` contains immutable sample records used across the app.

## Testing notes
This is a UI prototype without instrumentation tests. Manual walkthroughs are recommended after any changes:
- Launch each role from the login screen and step through all destinations.
- Toggle English ↔ Hindi from every top-bar.
- Run both text and voice symptom checks (voice flow requires a microphone).
- Confirm doctor call requests reflect Accept/Reject state and open the mock video UI.
- Load development screenshots to verify permission copy/paste instructions.

Enjoy the demo! The code is purposely annotated and modular so you can replace the mock layers with real services when the backend is ready.
