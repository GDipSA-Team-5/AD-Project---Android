# AD-Project---Android

Android app for the AD Project.

## Requirements
- Android Studio (latest stable recommended)
- JDK 17

## Run
1. Open this project in Android Studio.
2. Let Gradle sync.
3. Run the `app` configuration on an emulator or device.

## Tests
```bash
./gradlew testDebugUnitTest
```

```bash
./gradlew connectedAndroidTest
```

## CI
CI runs on push/PR to `main` with:
- Unit tests + JaCoCo coverage
- Android Lint
- SonarCloud analysis
- GitHub CodeQL scan

