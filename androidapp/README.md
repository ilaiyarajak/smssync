# SMS Sync

A simple personal-use Android app that automatically syncs bank transaction SMS messages to a custom HTTP endpoint.

## Features

### 1. SMS Listener
- Listens for incoming SMS using BroadcastReceiver
- Requests `RECEIVE_SMS` and `READ_SMS` permissions
- Extracts sender, timestamp, and message body
- Stores every SMS in Room database

### 2. Bank SMS Detection
Automatically detects bank transaction SMS by checking if:
- **Sender contains:** AX, VK, VM, JD, AD
- **Message contains:** credited, debited, spent, withdrawn, purchase, transaction, upi, balance, rs., inr, account

### 3. Auto Sync
When a bank SMS arrives:
- Immediately POSTs to configured HTTP URL
- On success: marks as `synced=true` with timestamp
- On failure: stores error message

### 4. Settings Screen
- Configure server URL
- Provide bearer token
- Test connection to server
- Settings stored in DataStore Preferences

### 5. Logs Screen
View all SMS with:
- Sender address
- Date and time
- Sync status
- Bank SMS flag
- Expandable rows showing full message and errors
- Individual sync button for each SMS

### 6. Master Sync Button
- Reads device SMS using ContentResolver
- Imports missing SMS into database
- Syncs all unsynced bank SMS
- Shows statistics: scanned, imported, synced, failed

## Technology Stack

- **Language:** Kotlin
- **Android SDK:** 35+
- **UI Framework:** Jetpack Compose
- **Database:** Room
- **Background Jobs:** WorkManager
- **Networking:** Retrofit + OkHttp
- **Preferences:** DataStore
- **Architecture:** MVVM

## Project Structure

```
androidapp/
├── app/
│   ├── src/main/
│   │   ├── kotlin/com/smssync/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── db/
│   │   │   │   │   ├── SmsEntity.kt
│   │   │   │   │   ├── SmsDao.kt
│   │   │   │   │   └── SmsDatabase.kt
│   │   │   │   └── repository/
│   │   │   │       └── SmsRepository.kt
│   │   │   ├── network/
│   │   │   │   └── SmsApiService.kt
│   │   │   ├── receiver/
│   │   │   │   └── SmsReceiver.kt
│   │   │   ├── ui/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── settings/
│   │   │   │   │   ├── SettingsScreen.kt
│   │   │   │   │   └── SettingsViewModel.kt
│   │   │   │   └── logs/
│   │   │   │       ├── LogsScreen.kt
│   │   │   │       └── LogsViewModel.kt
│   │   │   ├── worker/
│   │   │   │   └── SyncWorker.kt
│   │   │   └── util/
│   │   │       ├── BankSmsFilter.kt
│   │   │       └── PreferencesManager.kt
│   │   ├── AndroidManifest.xml
│   │   └── res/
│   │       └── values/
│   │           └── strings.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── gradle.properties
├── settings.gradle.kts
├── .github/workflows/
│   └── android-build.yml
└── README.md
```

## Database Schema

### SmsEntity Table
| Column | Type | Description |
|--------|------|-------------|
| id | Int | Primary key (auto-increment) |
| sender | String | Phone number of sender |
| body | String | Full SMS message |
| timestamp | Long | Time in milliseconds |
| isBankSms | Boolean | Whether message is bank transaction |
| synced | Boolean | Whether synced to server |
| syncedAt | Long? | Timestamp of sync (nullable) |
| errorMessage | String? | Error details if sync failed |

## Server API

The app sends POST requests to your configured endpoint:

```
POST {SERVER_URL}
Authorization: Bearer {AUTH_TOKEN}
Content-Type: application/json

{
  "sender": "AX-HDFCBK",
  "timestamp": 1711111111000,
  "message": "Rs.500 credited to your account...",
  "bankSms": true
}
```

**Success:** Any HTTP 200-299 response
**Failure:** Any other HTTP status or network error

## Building

### Prerequisites
- Android Studio Giraffe or later
- Android SDK 35
- JDK 17 or later

### Build Commands

```bash
# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease

# Run tests
./gradlew test
```

### Build Output
Generated APK: `app/build/outputs/apk/release/app-release.apk`

## Installation

1. Build the release APK:
   ```bash
   cd androidapp
   ./gradlew assembleRelease
   ```

2. Install on device:
   ```bash
   adb install app/build/outputs/apk/release/app-release.apk
   ```

3. Grant required permissions when prompted:
   - Read SMS
   - Receive SMS
   - Internet

4. Configure in Settings:
   - Enter your server URL
   - Enter bearer token
   - Test connection

## Permissions

Required permissions in `AndroidManifest.xml`:
- `android.permission.RECEIVE_SMS` - Listen for incoming SMS
- `android.permission.READ_SMS` - Read SMS messages
- `android.permission.INTERNET` - Send HTTP requests

## GitHub Actions

The repository includes a workflow (`.github/workflows/android-build.yml`) that:
- Builds on every push to `main` and `develop`
- Builds on pull requests
- Generates release APK
- Uploads APK as artifact
- Stores artifacts for 30 days

### Download APK from GitHub Actions
1. Go to repository Actions tab
2. Click on a successful build
3. Download the `app-release` artifact

## Architecture

### MVVM Pattern
- **Model:** Room database entities and repository
- **View:** Jetpack Compose UI screens
- **ViewModel:** LogsViewModel, SettingsViewModel

### Key Classes

**SmsRepository**
- Handles all SMS database operations
- Manages API communication
- Coordinates syncing logic

**SmsReceiver (BroadcastReceiver)**
- Listens for incoming SMS
- Auto-detects bank transactions
- Triggers immediate sync if bank SMS

**SmsViewModel**
- Manages SMS list state
- Handles master sync operation
- Updates sync statistics

**SettingsViewModel**
- Manages server URL and token
- Handles test connection
- Persists settings via DataStore

## UI Flow

```
┌─────────────────┐
│   Logs Screen   │
├─────────────────┤
│ Master Sync btn │
│ Sync Statistics │
│ SMS List Items  │
│ (expandable)    │
└────────┬────────┘
         │
         └──→ Settings Screen
              (gear icon)
```

## No Third-Party Services

- ✅ No Firebase
- ✅ No Analytics
- ✅ No Ads
- ✅ No User Authentication (bearer token only)
- ✅ No Cloud Storage
- ✅ 100% Local Processing

## Privacy

All SMS data is stored locally on your device. Only configured bank SMS messages are sent to your endpoint. No data is shared with third parties or cloud services.

## Debugging

### Logs
- Use Android Logcat to view app logs
- Use SMS List screen to see all received SMS
- Use Settings screen to test server connection

### Testing
1. Enable SMS in Android Emulator
2. Send test SMS from AVD Manager
3. Check if SMS appears in Logs screen
4. Verify sync status

## Build Size

- Debug APK: ~50MB
- Release APK: ~15-20MB (with ProGuard)

## Compatibility

- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 35 (Android 15)
- **Kotlin:** 1.9.10+
- **Gradle:** 8.4+

## Troubleshooting

### SMS Not Being Received
1. Check if permissions are granted
2. Verify `SmsReceiver` is registered in AndroidManifest
3. Check logcat for receiver errors

### Sync Failures
1. Verify server URL is correct (with protocol)
2. Test connection in Settings screen
3. Check error message in expanded SMS row

### Database Errors
1. Clear app data: Settings > Apps > SMS Sync > Storage > Clear All Data
2. Reinstall app

## License

This is a personal-use project. No license restrictions.

## Contributing

This is a personal-use app. Contributions for personal purposes only.

## Support

For issues, check:
1. Logcat output
2. Error messages in Logs screen
3. Test connection result in Settings
