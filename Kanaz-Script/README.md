# Kanaz Script

Advanced code editor for Android with support for large files and developer tools.

## Features
- Virtual rendering editor for files up to 50,000 lines
- Syntax highlighting for 15+ programming languages
- Integrated terminal emulator
- File explorer with tree view
- Git integration
- Multiple themes and customization

## Requirements
- Android 7.0+ (API 24+)
- Minimum 2GB RAM recommended
- 50MB free storage

## Performance Targets
- APK Size: < 15MB
- Startup Time: < 2 seconds
- RAM Usage: < 150MB
- Install Size: < 50MB

## Building
```bash
./gradlew assembleDebug

Testing 

./gradlew test
./gradlew connectedAndroidTest

Architecture

· MVVM Architecture
· Dependency Injection with Hilt
· Coroutines for async operations
· Room database for local storage

License

GPLv3
