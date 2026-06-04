# SMS Sync - Setup Guide

## Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/ilaiyarajak/smssync.git
cd smssync/androidapp
```

### 2. Open in Android Studio
1. Open Android Studio
2. Click "Open"
3. Select the `androidapp` folder
4. Wait for Gradle sync to complete

### 3. Build the App
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

### 4. Run on Device/Emulator
```bash
# Install and run
./gradlew installDebug

# Or directly from Android Studio: Run > Run 'app'
```

## Prerequisites

### System Requirements
- macOS, Linux, or Windows
- 4GB RAM minimum
- 10GB free disk space

### Software Requirements
- **Android Studio** 2023.1 or later (Giraffe)
- **JDK 17** or later
- **Android SDK 35** (Auto-installed by Android Studio)
- **Gradle 8.4** or later

### Install Android Studio
1. Download from https://developer.android.com/studio
2. Follow installation instructions for your OS
3. Open and complete initial setup

## Configuration

### Server Setup

1. Prepare your HTTP endpoint that accepts:
   ```json
   POST /your-endpoint
   Authorization: Bearer YOUR_TOKEN
   Content-Type: application/json
   
   {
     "sender": "AX-HDFCBK",
     "timestamp": 1711111111000,
     "message": "Rs.500 credited...",
     "bankSms": true
   }
   ```

2. Your endpoint should respond with HTTP 200-299 for success

### App Configuration

1. **Launch the app** on device/emulator
2. **Grant permissions** when prompted:
   - Allow Read SMS
   - Allow Receive SMS
   - Allow Internet
3. **Open Settings** (tap the gear icon at top)
4. **Enter Server Details:**
   - Server URL: `https://your-server.com/endpoint`
   - Bearer Token: Your authentication token
5. **Test Connection:** Click "Test Connection"
   - Should show "Connection successful"
6. **Save:** Click "Save"

## Device Setup

### On Android Device (Physical)

1. **Enable USB Debugging:**
   - Settings > About Phone > Tap "Build Number" 7 times
   - Settings > Developer Options > Enable USB Debugging
   
2. **Connect via USB:**
   ```bash
   adb devices  # Should list your device
   ```

3. **Install APK:**
   ```bash
   ./gradlew installDebug
   ```

4. **Grant Permissions:**
   - Open App > Grant SMS and Internet permissions

### On Android Emulator

1. **Create AVD (if needed):**
   - Android Studio > Tools > AVD Manager
   - Create Virtual Device with API 35

2. **Start Emulator:**
   ```bash
   emulator -avd YourAVDName
   ```

3. **Install:**
   ```bash
   ./gradlew installDebug
   ```

4. **Send Test SMS:**
   - AVD Manager > Your Device > ... > Send SMS
   - From: 1234567890
   - Message: "Rs. 500 credited to your account"

## Development

### Import into IDE

```bash
cd androidapp
# Android Studio will auto-detect and configure
```

### Build Tasks
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest

# Check for lint issues
./gradlew lint
```

### Key Files to Edit

- **Database Schema:** `app/src/main/kotlin/com/smssync/data/db/SmsEntity.kt`
- **API Service:** `app/src/main/kotlin/com/smssync/network/SmsApiService.kt`
- **SMS Filter:** `app/src/main/kotlin/com/smssync/util/BankSmsFilter.kt`
- **Logs UI:** `app/src/main/kotlin/com/smssync/ui/logs/LogsScreen.kt`
- **Settings UI:** `app/src/main/kotlin/com/smssync/ui/settings/SettingsScreen.kt`

## Debugging

### View Logs
```bash
# Real-time logs
adb logcat | grep "smssync"

# All logs
adb logcat
```

### Debug in Android Studio
1. Set breakpoint in code
2. Run > Debug 'app'
3. App will pause at breakpoint
4. Inspect variables in Variables panel

### Common Issues

#### "Build error: Gradle sync failed"
```bash
# Clean and resync
./gradlew clean
# Or: File > Sync Now in Android Studio
```

#### "SDK not found"
- Open SDK Manager in Android Studio
- Ensure API 35 is installed
- Update Android SDK tools

#### "Emulator doesn't see SMS"
- Use AVD Manager to send SMS
- Verify app has READ_SMS permission

#### "Server connection fails"
1. Verify server URL format: `https://your-server.com/endpoint`
2. Test endpoint manually with curl:
   ```bash
   curl -X POST https://your-server.com/endpoint \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"sender":"TEST","timestamp":'$(date +%s000)',"message":"test","bankSms":false}'
   ```
3. Check firewall/proxy settings

#### "App crashes on startup"
1. Check logcat for error
2. Ensure all permissions granted
3. Try clearing app data: Settings > Apps > SMS Sync > Storage > Clear All Data

## Release Build

### Create Signed APK
```bash
# Use Android Studio GUI:
# Build > Generate Signed Bundle / APK
# Select APK > Create new key store
# Fill in keystore details
# Select "release" build type
# Finish

# Or command line:
./gradlew assembleRelease
```

### APK Location
- **Debug:** `app/build/outputs/apk/debug/app-debug.apk`
- **Release:** `app/build/outputs/apk/release/app-release.apk`

### Distribution
1. Share APK via email or file sharing
2. Download APK on Android device
3. Open file manager > Tap APK to install
4. Grant install from unknown sources permission if prompted

## GitHub Actions

The repo includes automated build workflow.

### Trigger Build
- Push to `main` or `develop` branch
- Create pull request

### Download Artifacts
1. Go to repository Actions tab
2. Click on successful workflow run
3. Download `app-release` artifact

## Troubleshooting

### Gradle Sync Issues
```bash
# Update Gradle wrapper
./gradlew wrapper --gradle-version 8.4

# Clear cache
./gradlew cleanBuildCache

# Resync
./gradlew --refresh-dependencies
```

### Memory Issues During Build
```bash
# Increase heap size in gradle.properties
org.gradle.jvmargs=-Xmx2048m
```

### Permission Issues on Linux
```bash
# Make scripts executable
chmod +x ./gradlew
chmod +x ./app/src/main/kotlin/com/smssync/**
```

## Next Steps

1. **Test locally:** Send SMS on emulator, verify it appears in Logs
2. **Configure server:** Update server URL and token in Settings
3. **Test connection:** Click "Test Connection" in Settings
4. **Monitor syncs:** Watch Logs screen for sync status
5. **Iterate:** Use Master Sync to backfill SMS from device

## Additional Resources

- [Android Developer Docs](https://developer.android.com/)
- [Jetpack Compose Guide](https://developer.android.com/jetpack/compose)
- [Room Database Docs](https://developer.android.com/training/data-storage/room)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [DataStore Preferences](https://developer.android.com/jetpack/androidx/releases/datastore)

## Support

For issues:
1. Check logcat output
2. Verify all permissions are granted
3. Test server connection separately
4. Check AndroidManifest.xml for receiver registration
