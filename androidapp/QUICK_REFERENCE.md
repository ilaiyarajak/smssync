# SMS Sync - Quick Reference

## Quick Start (30 seconds)

```bash
# 1. Open in Android Studio
cd /path/to/androidapp

# 2. Build
./gradlew assembleRelease

# 3. Install
./gradlew installDebug

# 4. Configure in app settings
# - Server URL: https://your-api.com/endpoint
# - Bearer Token: your-token-here
# - Click "Test Connection"
```

## Key Files

### Database
- `SmsEntity.kt` - Table structure
- `SmsDao.kt` - Queries
- `SmsDatabase.kt` - Database instance

### API
- `SmsApiService.kt` - Retrofit service
- `SmsRepository.kt` - Data operations

### SMS Processing
- `SmsReceiver.kt` - Handles incoming SMS
- `BankSmsFilter.kt` - Bank SMS detection
- `SyncWorker.kt` - Background sync

### UI
- `LogsScreen.kt` - Main screen
- `LogsViewModel.kt` - Logs logic
- `SettingsScreen.kt` - Settings screen
- `SettingsViewModel.kt` - Settings logic

### Configuration
- `AndroidManifest.xml` - Permissions & receiver
- `build.gradle` - Dependencies
- `gradle.properties` - Gradle settings

## Bank SMS Detection

SMS marked as bank if:
- **Sender:** AX, VK, VM, JD, AD (case-insensitive)
- **Body:** credited, debited, spent, withdrawn, purchase, transaction, upi, balance, rs., inr, account

## API Request

```bash
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

## Gradle Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Install
./gradlew installDebug
./gradlew installRelease

# Clean
./gradlew clean

# Test
./gradlew test
```

## APK Locations

- **Debug:** `app/build/outputs/apk/debug/app-debug.apk`
- **Release:** `app/build/outputs/apk/release/app-release.apk`

## Permissions

```xml
android.permission.RECEIVE_SMS
android.permission.READ_SMS
android.permission.INTERNET
```

## Database Schema

```
SmsEntity {
  id: Int (primary key)
  sender: String
  body: String
  timestamp: Long
  isBankSms: Boolean
  synced: Boolean
  syncedAt: Long? (nullable)
  errorMessage: String? (nullable)
}
```

## UI Navigation

```
Logs Screen
├── Master Sync Button
├── Sync Statistics (after sync)
├── SMS List
│   └── Expandable Item
│       ├── Message Body
│       ├── Error (if any)
│       └── Sync Button
└── Settings Button (gear icon)
    └── Settings Screen
        ├── Server URL
        ├── Bearer Token
        ├── Save Button
        └── Test Connection Button
```

## Debugging Tips

```bash
# View logs
adb logcat | grep smssync

# Install and run
./gradlew installDebug
adb shell am start -n com.smssync/.MainActivity

# Clear data
adb shell pm clear com.smssync

# Check permissions
adb shell pm list permissions -d | grep sms
```

## Testing Workflow

1. **Emulator Setup:**
   ```bash
   emulator -avd YourAVD
   ```

2. **Send Test SMS:**
   - AVD Manager > Your Device > ... > Send SMS
   - From: 1234567890
   - Message: "Rs. 500 credited to your account"

3. **Check Logs:**
   - Open app > Should see SMS in list
   - Check "Bank SMS" tag

4. **Configure Server:**
   - Settings > Enter URL and token
   - Click "Test Connection"

5. **Monitor Sync:**
   - Send another SMS
   - Watch Logs screen for sync status

## Environment Setup

```bash
# Check Java version
java -version
# Expected: JDK 17+

# Check Gradle
./gradlew --version
# Expected: Gradle 8.4+

# Check Android SDK
adb --version

# List connected devices
adb devices
```

## Build Properties

File: `gradle.properties`

```properties
# Android Build System
android.useAndroidX=true
android.enableJetifier=true

# Gradle Options
org.gradle.jvmargs=-Xmx2048m
org.gradle.parallel=true
org.gradle.caching=true
```

## API Implementation Examples

### Node.js
```javascript
const express = require('express');
app.post('/sms', (req, res) => {
  const { sender, timestamp, message, bankSms } = req.body;
  // Process SMS
  res.json({ status: 'success' });
});
```

### Python
```python
from flask import Flask, request
@app.route('/sms', methods=['POST'])
def handle_sms():
    data = request.json
    # Process SMS
    return { 'status': 'success' }
```

### cURL
```bash
curl -X POST https://api.example.com/sms \
  -H "Authorization: Bearer token" \
  -H "Content-Type: application/json" \
  -d '{
    "sender":"AX-BANK",
    "timestamp":'$(date +%s000)',
    "message":"Rs.500 credited",
    "bankSms":true
  }'
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Build fails | Run `./gradlew clean` and retry |
| Sync fails | Check server URL and token in Settings |
| SMS not received | Check RECEIVE_SMS permission |
| Can't build | Update Android SDK to API 35 |
| Device not found | Run `adb devices` |

## Architecture Layers

```
UI Layer (Compose)
    ↓
ViewModel (State Management)
    ↓
Repository (Data Logic)
    ↓
┌─────────────────────┐
│  Room Database      │
│  Retrofit API       │
│  Preferences        │
└─────────────────────┘
```

## Performance Tips

- Master Sync reads all device SMS (can be slow on first run)
- Database queries use indexes on timestamp
- Compose recomposition optimized with remember {}
- API requests timeout after 30 seconds

## File Sizes

- App (debug): ~50MB
- App (release): ~15-20MB
- Database: Varies (depends on SMS count)
- Each SMS: ~1-2KB average

## Kotlin Version

- **Kotlin:** 1.9.10
- **Coroutines:** Built-in with kotlinx
- **Flow:** Used for reactive updates
- **Suspend functions:** For async operations

## Android Version Support

- **Min SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 35 (Android 15)
- **Compile SDK:** 35
- **Gradle Plugin:** 8.1.4

## Compose Version

- **Compose UI:** 1.6.0
- **Material3:** 1.1.2
- **Compiler Extension:** 1.5.3

## Room Version

- **Room Runtime:** 2.6.1
- **Annotation Processor:** kapt
- **Flow Support:** Yes

## Networking

- **Retrofit:** 2.9.0
- **OkHttp:** 4.11.0
- **GSON:** Built into Retrofit
- **Timeout:** 30 seconds

## Important Notes

1. No authentication beyond bearer token
2. No automatic retry (manual retry via UI)
3. All SMS stored locally
4. Bank SMS synced immediately
5. Non-bank SMS stored but not synced
6. Settings persist via DataStore

## Next Steps After Setup

1. Push to GitHub
2. Set up GitHub Actions secrets (optional)
3. Test with real device
4. Monitor logs
5. Integrate with server
6. Create APK distribution method
