#!/bin/bash

echo "Initializing Git repository..."
git init

echo "Adding all files..."
git add .

echo "Committing changes..."
git commit -m "Initial commit: Kanaz Script - Android Developer Editor

- Editor dengan dukungan file besar (hingga 10MB/50.000 baris)
- Syntax highlighting untuk 20+ bahasa pemrograman
- Git integration dengan JGit
- Jetpack Compose UI
- MVVM Architecture dengan Dagger Hilt
- Virtual rendering untuk performa optimal
- Multi-tab dan split-screen editing
- Terminal emulator terintegrasi
- REST API client
- JSON/XML formatter
- QR code generator/reader
- Performance targets: APK <15MB, startup <2s, RAM <150MB"

echo "Adding remote origin..."
git remote add origin https://github.com/username/kanaz-script.git

echo "Pushing to GitHub..."
git push -u origin main

echo "Done! Kanaz Script project has been pushed to GitHub."
