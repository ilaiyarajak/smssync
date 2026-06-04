# SMS Sync - API Documentation

## Server Endpoint Specification

### Request Format

**Method:** `POST`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "sender": "AX-HDFCBK",
  "timestamp": 1711111111000,
  "message": "Rs.500 credited to your account. Balance: Rs.5000. Ref: ABC123",
  "bankSms": true
}
```

### Field Descriptions

| Field | Type | Description | Example |
|-------|------|-------------|---------|
| sender | String | Originating phone number or SMS sender ID | "AX-HDFCBK", "+919876543210" |
| timestamp | Long | Unix timestamp in milliseconds | 1711111111000 |
| message | String | Full SMS message body | "Rs.500 credited to your account" |
| bankSms | Boolean | Whether app detected this as bank SMS | true |

### Success Response

**HTTP Status:** 200-299 (Success)

Any response with status 200-299 is considered successful. Response body can be any format.

**Example:**
```
HTTP/1.1 200 OK
Content-Type: application/json

{
  "status": "success",
  "message": "SMS received"
}
```

### Failure Response

**HTTP Status:** Any other status (3xx, 4xx, 5xx)

Treated as failure. App will:
- Mark SMS as not synced
- Store error message
- Allow manual retry from Logs screen

**Example:**
```
HTTP/1.1 401 Unauthorized

{
  "error": "Invalid token"
}
```

## Implementation Examples

### Node.js / Express

```javascript
const express = require('express');
const app = express();

app.use(express.json());

const validateBearer = (req, res, next) => {
  const auth = req.headers.authorization;
  const token = auth?.replace('Bearer ', '');
  
  if (token !== process.env.SMS_TOKEN) {
    return res.status(401).json({ error: 'Unauthorized' });
  }
  next();
};

app.post('/sms-webhook', validateBearer, (req, res) => {
  const { sender, timestamp, message, bankSms } = req.body;
  
  console.log(`SMS from ${sender} at ${new Date(timestamp)}`);
  console.log(`Bank SMS: ${bankSms}`);
  console.log(`Message: ${message}`);
  
  // Process SMS
  // Save to database, send email, etc.
  
  res.json({ status: 'success' });
});

app.listen(3000, () => console.log('Listening on port 3000'));
```

### Python / Flask

```python
from flask import Flask, request, jsonify
import os
from datetime import datetime

app = Flask(__name__)

def verify_bearer(request):
    auth = request.headers.get('Authorization', '')
    token = auth.replace('Bearer ', '')
    return token == os.environ.get('SMS_TOKEN')

@app.route('/sms-webhook', methods=['POST'])
def handle_sms():
    if not verify_bearer(request):
        return jsonify({'error': 'Unauthorized'}), 401
    
    data = request.json
    sender = data.get('sender')
    timestamp = data.get('timestamp')
    message = data.get('message')
    bank_sms = data.get('bankSms')
    
    dt = datetime.fromtimestamp(timestamp / 1000)
    print(f"SMS from {sender} at {dt}")
    print(f"Bank SMS: {bank_sms}")
    print(f"Message: {message}")
    
    # Process SMS
    # Save to database, send email, etc.
    
    return jsonify({'status': 'success'}), 200

if __name__ == '__main__':
    app.run(port=3000, debug=True)
```

### Go / Gin

```go
package main

import (
	"github.com/gin-gonic/gin"
	"log"
	"net/http"
	"os"
	"strings"
	"time"
)

type SmsPayload struct {
	Sender   string `json:"sender"`
	Timestamp int64  `json:"timestamp"`
	Message  string `json:"message"`
	BankSms  bool   `json:"bankSms"`
}

func verifyBearer(c *gin.Context) {
	auth := c.GetHeader("Authorization")
	token := strings.TrimPrefix(auth, "Bearer ")
	
	if token != os.Getenv("SMS_TOKEN") {
		c.JSON(http.StatusUnauthorized, gin.H{"error": "Unauthorized"})
		c.Abort()
		return
	}
	c.Next()
}

func handleSms(c *gin.Context) {
	var payload SmsPayload
	if err := c.BindJSON(&payload); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	
	dt := time.UnixMilli(payload.Timestamp)
	log.Printf("SMS from %s at %v\n", payload.Sender, dt)
	log.Printf("Bank SMS: %v\n", payload.BankSms)
	log.Printf("Message: %s\n", payload.Message)
	
	// Process SMS
	// Save to database, send email, etc.
	
	c.JSON(http.StatusOK, gin.H{"status": "success"})
}

func main() {
	router := gin.Default()
	router.POST("/sms-webhook", verifyBearer, handleSms)
	router.Run(":3000")
}
```

### Java / Spring Boot

```java
@RestController
@RequestMapping("/sms-webhook")
public class SmsController {
    
    private static final String EXPECTED_TOKEN = System.getenv("SMS_TOKEN");
    
    @PostMapping
    public ResponseEntity<Map<String, String>> handleSms(
            @RequestHeader("Authorization") String auth,
            @RequestBody SmsPayload payload) {
        
        String token = auth.replace("Bearer ", "");
        if (!token.equals(EXPECTED_TOKEN)) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized"));
        }
        
        LocalDateTime dt = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(payload.getTimestamp()),
            ZoneId.systemDefault()
        );
        
        System.out.println("SMS from " + payload.getSender() + " at " + dt);
        System.out.println("Bank SMS: " + payload.isBankSms());
        System.out.println("Message: " + payload.getMessage());
        
        // Process SMS
        // Save to database, send email, etc.
        
        return ResponseEntity.ok(Map.of("status", "success"));
    }
}
```

## Testing

### Test with cURL

```bash
# Set your token and server URL
TOKEN="your-auth-token"
SERVER_URL="https://your-server.com/sms-webhook"

# Send test SMS
curl -X POST $SERVER_URL \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sender": "AX-HDFCBK",
    "timestamp": '$(date +%s000)',
    "message": "Rs.500 credited to your account. Balance: Rs.5000",
    "bankSms": true
  }'
```

### Test with Postman

1. Open Postman
2. Create new POST request
3. URL: `https://your-server.com/sms-webhook`
4. Headers tab:
   - `Authorization: Bearer your-token`
   - `Content-Type: application/json`
5. Body (raw, JSON):
   ```json
   {
     "sender": "AX-HDFCBK",
     "timestamp": 1711111111000,
     "message": "Rs.500 credited to your account",
     "bankSms": true
   }
   ```
6. Click Send

### Test in App

1. Open SMS Sync app
2. Go to Settings
3. Enter Server URL and Token
4. Click "Test Connection"
5. Should show "Connection successful"

## Error Handling

### Retry Logic

The app **does NOT automatically retry** failed syncs. You must:
1. Go to Logs screen
2. Find failed SMS (marked as "Not Synced")
3. Expand the row to see error message
4. Click "Sync" button to retry

### Common Errors

| Error | Cause | Solution |
|-------|-------|----------|
| Connection failed | Server unreachable | Check URL, verify server is running |
| Unauthorized (401) | Invalid token | Verify token in Settings |
| Bad Request (400) | Invalid JSON | Check request body format |
| Internal Server (500) | Server error | Check server logs |
| Network timeout | Server slow | Increase timeout, check network |

## Webhook Delivery Guarantees

**Best Effort Delivery:**
- App sends SMS when it arrives (if bank SMS detected)
- If server doesn't respond, SMS is marked as failed
- User must manually retry or use Master Sync

**No Guarantee of Delivery:**
- If app is uninstalled, SMS is lost
- If server is down for long time, SMS may be dropped
- If device is offline, SMS may be delayed

## Rate Limiting

The app sends one SMS per request. Consider:
- Incoming rate: Limited by device's SMS rate (unlikely to exceed 100/hour)
- No burst protection needed for personal use
- Implement if needed on server side

## Data Privacy

All data stays on user's device except for bank SMS:
- Entire SMS body sent to endpoint
- No user identification (only phone number)
- No device metadata sent
- User controls which endpoint receives data

## Logging

Enable logging for debugging:

**Android Studio:**
1. Open Logcat
2. Filter: `com.smssync`
3. Send SMS to device
4. Watch logs in real-time

**adb:**
```bash
adb logcat | grep smssync
```

## Limits

| Item | Limit | Notes |
|------|-------|-------|
| Message Size | ~1600 chars | Standard SMS limit |
| Sender Length | ~20 chars | Phone number or ID |
| Request Timeout | 30 seconds | Hardcoded in app |
| Database Size | ~500MB | Device storage limit |
| Local SMS History | All available | Limited by device storage |

## Future Enhancements

Possible future additions:
- Batch SMS uploads
- Offline queue with retry
- Custom filter rules
- Webhook signatures (HMAC)
- Request rate limiting
- SMS categorization
