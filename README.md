# AI TV

A starter Android TV app with a remote-friendly discovery screen and an AI-assistant experience for finding shows by mood, genre, or story.

## What's included

- Native Android app built with Kotlin and Jetpack Compose.
- Leanback launcher registration and TV banner; touch input is optional.
- D-pad-friendly catalog cards, title details, search field, and quick prompts.
- Local recommendation fallback so the demo works without credentials or network access.
- Optional backend URL setting for adding a real AI service without shipping a provider secret in the APK.

The included catalog is sample content only. It does not stream video or include licensed titles.

## Open and run

1. Open this repository in Android Studio (Giraffe or newer) and allow Gradle sync.
2. Select the `app` run configuration and launch on an Android TV emulator or device.
3. For a real AI integration, implement the backend contract below and set `AI_API_BASE_URL` in your local `~/.gradle/gradle.properties`:

```properties
AI_API_BASE_URL=https://your-backend.example.com
```

The app must call your backend, which can then call an AI provider using a server-side secret. Never put provider API keys in the Android app or commit them to this repository.

## Backend integration contract

The planned endpoint is `POST {AI_API_BASE_URL}/recommendations`, with JSON request fields `query` and `catalog`. Return JSON such as:

```json
{
  "reply": "For a relaxed evening, try Wild Neighbors.",
  "titles": ["Wild Neighbors"]
}
```

Use a licensed content metadata/playback provider before enabling real playback. Add authentication, rate limiting, and privacy protections on the backend before deployment.
