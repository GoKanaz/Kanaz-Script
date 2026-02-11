#!/bin/bash
cd "$(dirname "$0")"
chmod +x gradlew
./gradlew clean
./gradlew assembleDebug
if [ $? -eq 0 ]; then
    echo "Build successful!"
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
    if [ -f "$APK_PATH" ]; then
        APK_SIZE=$(stat -f%z "$APK_PATH" 2>/dev/null || stat -c%s "$APK_PATH")
        echo "APK Size: $((APK_SIZE/1024/1024))MB"
    fi
else
    echo "Build failed!"
    exit 1
fi
