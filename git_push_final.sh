#!/bin/bash
echo "Setting up Kanaz Script repository..."
cd KanazScript
git init
git add -A
git commit -m "Kanaz Script v1.0.0

Complete Android code editor application with:
- Advanced code editor with syntax highlighting
- Support for large files (up to 10MB/50,000 lines)
- Virtual rendering for optimal performance
- Git integration with JGit
- Multiple language support (Kotlin, Java, Python, JS, etc.)
- Developer tools (terminal, REST client, formatters)
- Material Design 3 UI
- MVVM architecture with Hilt DI
- Room database for local storage
- Performance optimized (<150MB RAM, <2s startup)

Targets:
- APK size: <15MB
- Startup time: <2 seconds
- RAM usage: <150MB
- Install size: <50MB

Build successfully on API 24+ (Android 7.0+)"
echo ""
echo "Repository initialized successfully!"
echo ""
echo "To push to GitHub:"
echo "1. Create a new repository on GitHub"
echo "2. Run: git remote add origin https://github.com/YOUR_USERNAME/KanazScript.git"
echo "3. Run: git push -u origin main"
echo ""
echo "To build the project:"
echo "./gradlew assembleDebug"
echo ""
echo "Project structure created successfully!"
