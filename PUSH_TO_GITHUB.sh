#!/bin/bash
cd "$(dirname "$0")"
git init
git config user.email "kanaz@script.dev"
git config user.name "Kanaz Script"
git add -A
git commit -m "Kanaz Script v1.0.0 - Complete Android Code Editor"
echo "Repository ready!"
echo "To push to GitHub:"
echo "1. Create repo at https://github.com/new"
echo "2. Run: git remote add origin https://github.com/YOUR_USERNAME/KanazScript.git"
echo "3. Run: git push -u origin main"
