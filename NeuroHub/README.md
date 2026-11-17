# NeuroHub

Android AI hub with automatic model routing. This is the first runnable project scaffold.

## Structure
- android/: Kotlin + Jetpack Compose client
- backend/: FastAPI service; provider credentials remain server-side
- docs/: architecture notes

## Current status
The Android app is an initial UI prototype. The backend health endpoint and chat placeholder are implemented. Real provider adapters, authentication, persistent conversation storage, budget controls and local inference are next milestones.

## Build
GitHub Actions builds a debug APK on pushes to this branch. Download it from the workflow run's Artifacts section.

## Security
Never place provider API keys in the Android app or commit real secrets. Configure them as server environment secrets when the backend is deployed.
