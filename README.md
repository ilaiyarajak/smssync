# SMS Sync - Android Application

Personal-use Android app that automatically syncs bank transaction SMS messages to a custom HTTP endpoint.

## 📍 Project Structure

```
smssync/
├── .github/
│   └── workflows/
│       └── android-build.yml        ✅ GitHub Actions CI/CD
├── README.md                         ← You are here
├── 00_WORKFLOW_MIGRATION_COMPLETE.md
├── WORKFLOW_MIGRATION.md
└── androidapp/                       ← Android project
    ├── README.md                     ← App documentation
    ├── SETUP.md                      ← Setup guide
    ├── API.md                        ← API documentation
    └── ... (complete Android app)
```

## ✅ What's Inside

### 📱 Android Application (`androidapp/`)
- **15 Kotlin source files** - Complete app logic
- **Jetpack Compose UI** - Modern Android UI framework
- **Room Database** - Local SMS storage
- **Retrofit API** - Server integration
- **10 documentation files** - Comprehensive guides

### 🔧 GitHub Actions
- **Automated builds** on push to main/develop
- **Debug APK** generation
- **Release APK** generation
- **Artifact upload** with 7-30 day retention
- **PR commenting** with build status

### 📚 Documentation
- **SETUP.md** - Step-by-step setup
- **API.md** - Server endpoint documentation
- **QUICK_REFERENCE.md** - Quick commands
- **And 7 more guides** - Complete reference

## 🚀 Quick Start

### 1. Open Android Project
```bash
cd androidapp
```

### 2. Read Documentation
```bash
cat README.md
cat SETUP.md
```

### 3. Build Locally
```bash
./gradlew assembleRelease
```

### 4. Watch CI/CD
```
Push to GitHub → Actions tab → See automatic builds
```

## 📋 GitHub Actions Workflow

**Location:** `.github/workflows/android-build.yml`

**What it does:**
- ✅ Builds on every push to main/develop
- ✅ Generates debug and release APKs
- ✅ Uploads artifacts to GitHub
- ✅ Comments on pull requests
- ✅ Stores artifacts for 7-30 days

## 📚 Documentation Files

### At Repository Root
- `00_WORKFLOW_MIGRATION_COMPLETE.md` - Workflow migration guide
- `WORKFLOW_MIGRATION.md` - Detailed migration info
- `WORKFLOW_SETUP_COMPLETE.md` - Setup verification

### In `androidapp/`
- `README.md` - App overview
- `SETUP.md` - Build and install
- `API.md` - Server API docs
- `QUICK_REFERENCE.md` - Quick commands
- `PROJECT_SUMMARY.md` - Technical details
- And 5 more...

## 🎯 Next Steps

1. **Explore the project**
   ```bash
   cd androidapp
   ls -la
   ```

2. **Read the app documentation**
   ```bash
   cat androidapp/README.md
   ```

3. **Follow the setup guide**
   ```bash
   cat androidapp/SETUP.md
   ```

4. **Build the app**
   ```bash
   cd androidapp
   ./gradlew assembleRelease
   ```

## 🔍 Key Features

✅ SMS Listener - Captures incoming SMS  
✅ Bank SMS Detection - Identifies bank transactions  
✅ Auto Sync - Posts to HTTP endpoint  
✅ Local Database - Stores all SMS  
✅ Settings Screen - Configure server/token  
✅ Logs Screen - View SMS with details  
✅ Master Sync - Bulk import and sync  
✅ MVVM Architecture - Clean code structure  
✅ GitHub Actions - Automated CI/CD  

## 📞 For More Information

- **Build instructions:** `androidapp/SETUP.md`
- **API documentation:** `androidapp/API.md`
- **Quick reference:** `androidapp/QUICK_REFERENCE.md`
- **Project details:** `androidapp/PROJECT_SUMMARY.md`
- **Workflow details:** `00_WORKFLOW_MIGRATION_COMPLETE.md`

## ✨ Status

✅ Android app complete and tested  
✅ GitHub Actions workflow configured  
✅ Documentation comprehensive  
✅ Ready for CI/CD activation  
✅ Ready for deployment  

---

**Version:** 1.0.0  
**Status:** Ready for use  
**Last Updated:** June 4, 2026
