#!/bin/bash

echo "Building Kanaz Script..."
cd "$(dirname "$0")"

echo "Cleaning project..."
./gradlew clean

echo "Building debug APK..."
./gradlew assembleDebug

echo "Building release APK..."
./gradlew assembleRelease

echo "Running tests..."
./gradlew test

echo "Build completed!"
echo "Debug APK: app/build/outputs/apk/debug/app-debug.apk"
echo "Release APK: app/build/outputs/apk/release/app-release.apk"
