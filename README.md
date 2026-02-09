# AD-Project---Android

Android app for the AD Project.

## Requirements
- Android Studio (latest stable)
- JDK 17
- Android SDK + emulator/device

## Setup
1. Open the project in Android Studio.
2. Let Gradle sync finish.
3. Run the `app` configuration on an emulator or device.

## Local Commands
Unit tests:
```bash
./gradlew :app:testDebugUnitTest
```

Coverage report:
```bash
./gradlew :app:jacocoTestReport
```

Lint report:
```bash
./gradlew :app:lintDebug
```

Instrumented UI tests:
```bash
./gradlew :app:connectedDebugAndroidTest
```

## CI / Security
Workflows run on PRs and pushes to `main`:
- Unit tests + JaCoCo coverage
- Android Lint
- SonarCloud analysis (quality gate)
- CodeQL (SAST)
- Snyk (SCA)

## Reports
- GitHub Pages (Lint + JaCoCo + links):  
  `https://gdipsa-team-5.github.io/AD-Project---Android/`
- SonarCloud dashboard:  
  `https://sonarcloud.io/dashboard?id=GDipSA-Team-5_AD-Project---Android`
- CodeQL alerts:  
  `https://github.com/GDipSA-Team-5/AD-Project---Android/security/code-scanning`
