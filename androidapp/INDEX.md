# SMS Sync Android App - Complete Documentation Index

## 📋 Overview
This is a complete Android application project that syncs bank transaction SMS to a custom HTTP endpoint. All source code, configuration, and documentation are included.

## 📁 Directory Structure

```
androidapp/
├── Documentation Files (Read First)
│   ├── README.md                    # Start here - Project overview
│   ├── SETUP.md                     # Setup & development guide
│   ├── QUICK_REFERENCE.md           # Quick lookup guide
│   ├── API.md                       # Server API documentation
│   ├── PROJECT_SUMMARY.md           # Complete project summary
│   ├── CHECKLIST.md                 # Build & testing checklist
│   └── INDEX.md                     # This file
│
├── Build Configuration
│   ├── build.gradle                 # Root build config
│   ├── gradle.properties            # Gradle settings
│   ├── settings.gradle.kts          # Project settings
│   ├── gradle/                      # Gradle wrapper scripts
│   ├── gradlew                      # Unix gradle wrapper
│   └── gradlew.bat                  # Windows gradle wrapper
│
├── GitHub Integration
│   └── .github/workflows/
│       └── android-build.yml        # CI/CD workflow
│
├── Source Code
│   └── app/
│       ├── build.gradle             # App build config
│       ├── proguard-rules.pro       # Obfuscation rules
│       └── src/main/
│           ├── kotlin/com/smssync/
│           │   ├── MainActivity.kt           # Main activity
│           │   ├── data/
│           │   │   ├── db/
│           │   │   │   ├── SmsEntity.kt     # Database entity
│           │   │   │   ├── SmsDao.kt        # Data access object
│           │   │   │   └── SmsDatabase.kt   # Database singleton
│           │   │   └── repository/
│           │   │       └── SmsRepository.kt # Data repository
│           │   ├── network/
│           │   │   └── SmsApiService.kt     # API service (Retrofit)
│           │   ├── receiver/
│           │   │   └── SmsReceiver.kt       # SMS broadcast receiver
│           │   ├── ui/
│           │   │   ├── MainActivity.kt      # Activity container
│           │   │   ├── settings/
│           │   │   │   ├── SettingsScreen.kt       # Settings UI
│           │   │   │   └── SettingsViewModel.kt    # Settings logic
│           │   │   └── logs/
│           │   │       ├── LogsScreen.kt          # Logs UI
│           │   │       └── LogsViewModel.kt       # Logs logic
│           │   ├── worker/
│           │   │   └── SyncWorker.kt       # Background worker
│           │   └── util/
│           │       ├── BankSmsFilter.kt    # SMS filter logic
│           │       └── PreferencesManager.kt # Settings storage
│           ├── AndroidManifest.xml        # App manifest
│           └── res/
│               └── values/
│                   ├── colors.xml         # Color resources
│                   ├── strings.xml        # String resources
│                   └── themes.xml         # Theme definitions
│
└── Git Configuration
    ├── .gitignore                   # Git ignore rules
    └── .gitattributes               # Git line endings
```

## 📚 Documentation Files

### Quick Start
1. **[README.md](README.md)** - Start here
   - Project overview and features
   - Technology stack
   - Architecture overview
   - Building and installation

2. **[SETUP.md](SETUP.md)** - Development setup
   - Prerequisites and installation
   - Configuration steps
   - Development workflow
   - Troubleshooting guide

3. **[QUICK_REFERENCE.md](QUICK_REFERENCE.md)** - Quick lookup
   - Quick start commands
   - Key files reference
   - Common commands
   - Debugging tips

### Detailed Documentation
4. **[API.md](API.md)** - Server API
   - Endpoint specification
   - Request/response format
   - Implementation examples (Node.js, Python, Go, Java)
   - Testing guide with cURL and Postman

5. **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - Project details
   - Complete file structure
   - Features implementation status
   - Technology stack details
   - Performance characteristics

6. **[CHECKLIST.md](CHECKLIST.md)** - Build verification
   - Pre-build checklist
   - Testing checklist
   - Code quality checklist
   - Release checklist

## 🚀 Quick Start

```bash
# 1. Navigate to project
cd /path/to/smssync/androidapp

# 2. Build release APK
./gradlew assembleRelease

# 3. Install on device
./gradlew installDebug

# 4. Configure in app
# - Open Settings (gear icon)
# - Enter server URL
# - Enter bearer token
# - Click "Test Connection"
```

## 🏗️ Architecture Overview

### Layers
```
UI Layer (Jetpack Compose)
    ↓
ViewModel (State Management)
    ↓
Repository (Business Logic)
    ↓
Data Layer (Room + API)
```

### Key Components
- **SmsEntity** - Database model
- **SmsRepository** - Data operations
- **SmsApiService** - API calls
- **SmsReceiver** - Incoming SMS listener
- **LogsScreen** - Main UI
- **SettingsScreen** - Configuration UI

## 🔧 Key Technologies

| Component | Library | Version |
|-----------|---------|---------|
| Language | Kotlin | 1.9.10 |
| Android | SDK 35 | Latest |
| UI | Jetpack Compose | 1.6.0 |
| Database | Room | 2.6.1 |
| HTTP | Retrofit + OkHttp | 2.9.0 |
| Async | Coroutines | Built-in |
| Storage | DataStore | 1.0.0 |

## 📱 Supported Devices

- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 35 (Android 15)
- **Architectures:** arm64-v8a, armeabi-v7a, x86, x86_64

## 💾 Database Schema

```sql
CREATE TABLE sms (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sender TEXT NOT NULL,
    body TEXT NOT NULL,
    timestamp LONG NOT NULL,
    isBankSms INTEGER NOT NULL,
    synced INTEGER NOT NULL DEFAULT 0,
    syncedAt LONG,
    errorMessage TEXT
)
```

## 📡 API Endpoint

**Request:**
```
POST {SERVER_URL}
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "sender": "AX-HDFCBK",
  "timestamp": 1711111111000,
  "message": "Rs.500 credited...",
  "bankSms": true
}
```

**Success:** HTTP 200-299
**Failure:** Any other status

## 🎯 Features

✅ SMS Listener
✅ Bank SMS Detection
✅ Auto Sync to Server
✅ Settings Screen
✅ Logs Screen
✅ Master Sync
✅ Database Storage
✅ MVVM Architecture
✅ GitHub Actions CI/CD

## 🔐 Permissions Required

```xml
android.permission.RECEIVE_SMS
android.permission.READ_SMS
android.permission.INTERNET
```

## 📦 Build Commands

```bash
# Clean build
./gradlew clean

# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Check lint
./gradlew lint
```

## 🐛 Debugging

```bash
# View app logs
adb logcat | grep smssync

# Install app
./gradlew installDebug

# Clear app data
adb shell pm clear com.smssync

# List connected devices
adb devices
```

## 📊 File Statistics

- **Kotlin Files:** 15
- **XML Files:** 6
- **Gradle Files:** 3
- **Configuration Files:** 5
- **Documentation Files:** 6
- **Total Lines of Code:** ~1500 (Kotlin)
- **Total Documentation:** ~2000 lines

## 🔍 What's Included

✅ Complete Kotlin source code
✅ Room database setup
✅ Retrofit API integration
✅ Jetpack Compose UI
✅ DataStore configuration
✅ BroadcastReceiver SMS listener
✅ MVVM architecture
✅ GitHub Actions workflow
✅ Gradle configuration
✅ Complete documentation
✅ Setup guide
✅ API documentation
✅ Quick reference guide

## ❌ What's NOT Included

❌ Firebase
❌ Analytics
❌ Ads
❌ User authentication (bearer token only)
❌ Cloud storage
❌ Third-party services

## 🚦 Getting Started Guide

### Step 1: Prerequisites
- Android Studio (Giraffe or later)
- JDK 17+
- Android SDK 35

### Step 2: Open Project
1. Android Studio > Open
2. Select `androidapp` folder
3. Wait for Gradle sync

### Step 3: Build
```bash
./gradlew assembleDebug
```

### Step 4: Install
```bash
./gradlew installDebug
```

### Step 5: Configure
1. Open app
2. Tap gear icon
3. Enter server URL
4. Enter bearer token
5. Click "Test Connection"

### Step 6: Test
1. Send test SMS
2. Check Logs screen
3. Verify sync status

## 📋 Documentation Map

| Document | Purpose | Audience |
|----------|---------|----------|
| README.md | Project overview | Everyone |
| SETUP.md | Development setup | Developers |
| API.md | Server integration | Backend developers |
| QUICK_REFERENCE.md | Quick lookup | Active developers |
| PROJECT_SUMMARY.md | Technical details | Architects |
| CHECKLIST.md | Build verification | QA/Release |

## 🎓 Learning Resources

- [Android Developer Docs](https://developer.android.com/)
- [Jetpack Compose Guide](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Retrofit](https://square.github.io/retrofit/)
- [DataStore](https://developer.android.com/jetpack/androidx/releases/datastore)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

## 🤝 Contributing

This is a personal-use project. Modifications welcome for personal purposes.

## 📝 License

Personal-use project. No commercial restrictions.

## 🆘 Support

### For Setup Issues
See [SETUP.md](SETUP.md) Troubleshooting section

### For Build Issues
Check [CHECKLIST.md](CHECKLIST.md) Build Process section

### For API Integration
See [API.md](API.md) for implementation examples

### For Quick Reference
See [QUICK_REFERENCE.md](QUICK_REFERENCE.md)

## 📞 Quick Links

- 🏠 [Project Home](README.md)
- ⚙️ [Setup Guide](SETUP.md)
- 🔍 [Quick Reference](QUICK_REFERENCE.md)
- 🌐 [API Documentation](API.md)
- 📊 [Project Summary](PROJECT_SUMMARY.md)
- ✅ [Build Checklist](CHECKLIST.md)

## 🎉 Next Steps

1. Read [README.md](README.md) for overview
2. Follow [SETUP.md](SETUP.md) for setup
3. Build with `./gradlew assembleRelease`
4. Test with [CHECKLIST.md](CHECKLIST.md)
5. Integrate API using [API.md](API.md)
6. Reference [QUICK_REFERENCE.md](QUICK_REFERENCE.md) during development

---

**Last Updated:** June 4, 2026
**Project Version:** 1.0.0
**Status:** Ready for use
