# Taskly - Android Event Manager App

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)


## Table of Contents
- [Features](#features)
- [Installation](#installation)
- [Project Structure](#project-structure)
- [Core Components](#core-components)
- [Database Schema](#database-schema)
- [API Reference](#api-reference)
- [Usage Guide](#usage-guide)
- [Permissions](#permissions)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [Screenshots](#screenshots)
- [License](#license)

## Features

### Core Functionality
- 📝 Task management (CRUD operations)
- ⏰ Time-based reminders
- 🔄 Data persistence
- 📱 Responsive UI

### Technical Highlights
- Material Design components
- RecyclerView with swipe gestures
- SQLite database with helper class
- AlarmManager for reminders
- BroadcastReceivers for system events

## Installation

### Requirements
- Android Studio Flamingo (2022.2.1) or later
- Android SDK 33 (API Level 33)
- Java 17

### Setup Instructions
1. Clone the repository:
   ```sh
   git clone https://github.com/Bitxo92/taskly.git
   cd taskly
   ```
2. Import into Android Studio:
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete
3. Build and Run:
   - Connect an Android device or start an emulator
   - Click "Run" (Shift+F10)
   - Select your target device

## Project Structure
```
app/
├── manifests/
│   └── AndroidManifest.xml
├── java/
│   └── com.patino.todolistapp/
│       ├── activities/
│       │   ├── AddTaskActivity.java
│       │   ├── EditTaskActivity.java
│       │   ├── MainActivity.java
│       │   └── SplashActivity.java
│       ├── adapters/
│       │   └── TaskAdapter.java
│       ├── database/
│       │   └── DatabaseHelper.java
│       ├── models/
│       │   └── Task.java
│       └── receivers/
│           ├── BootReceiver.java
│           └── TaskReminderReceiver.java
└── res/
    ├── layout/
    │   ├── activity_add_task.xml
    │   ├── activity_main.xml
    │   └── activity_splash.xml
    └── values/
        ├── colors.xml
        ├── strings.xml
        └── styles.xml
```

## Core Components

### 1. Database Helper
```java
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table creation SQL
    private static final String CREATE_TABLE = 
        "CREATE TABLE " + TABLE_TASKS + "(" +
        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_TITLE + " TEXT, " +
        COLUMN_DESCRIPTION + " TEXT, " +
        COLUMN_TIMESTAMP + " INTEGER)";
}
```

### 2. Task Model
```java
public class Task {
    private int id;
    private String title;
    private String description;
    private long timestamp;
    
    // Constructor and getters
}
```

### 3. Alarm Scheduling
```java
public static void scheduleTaskReminder(Context context, long taskTime, String taskTitle) {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    long reminderTime = taskTime - (10 * 60 * 1000); // 10 minutes before
    
    Intent intent = new Intent(context, TaskReminderReceiver.class);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(
        context, 
        (int) taskTime, 
        intent, 
        PendingIntent.FLAG_IMMUTABLE
    );
    
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP, 
        reminderTime, 
        pendingIntent
    );
}
```

## Database Schema
| Column      | Type    | Description                    |
|------------|--------|--------------------------------|
| id         | INTEGER | Primary key (auto-increment)  |
| title      | TEXT    | Task title (required)         |
| description | TEXT    | Task details                  |
| timestamp  | INTEGER | Unix timestamp (milliseconds) |

## API Reference

### MainActivity Methods
| Method | Description |
|--------|-------------|
| loadTasks() | Loads tasks from database |
| enableSwipeToDelete() | Configures swipe gestures |
| rescheduleAlarmsForAllTasks() | Restores alarms on app start |

### DatabaseHelper Methods
| Method | Description |
|--------|-------------|
| addTask() | Inserts new task |
| getAllTasks() | Retrieves all tasks |
| updateTask() | Modifies existing task |
| deleteTask() | Removes task |

## Usage Guide

### Adding a Task
1. Tap the floating action button (+)
2. Enter task details:
   - Title (required)
   - Description (optional)
   - Due date/time
3. Tap "Save"

### Managing Tasks
| Action | Gesture |
|--------|---------|
| Edit | Long-press |
| Delete | Swipe left |
| View | Normal tap |

### Notification Behavior
- Triggers 10 minutes before due time
- Includes vibration
- Survives device reboots

## Permissions
```xml
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
<uses-permission android:name="android.permission.VIBRATE"/>
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM"/>
```

## Testing
```sh
./gradlew test
```
```sh
./gradlew connectedAndroidTest
```

## Troubleshooting
| Issue | Solution |
|-------|----------|
| Alarms not firing | Verify exact alarm permission |
| Database not updating | Check for unclosed database connections |
| UI not refreshing | Ensure notifyDataSetChanged() is called |

## Contributing
1. Fork the repository
2. Create your feature branch:
   ```sh
   git checkout -b feature/new-feature
   ```
3. Commit your changes:
   ```sh
   git commit -m 'Add some feature'
   ```
4. Push to the branch:
   ```sh
   git push origin feature/new-feature
   ```
5. Open a pull request

## Screenshots
![image](https://github.com/user-attachments/assets/32428cbc-d0e3-4f08-9049-b139d9c375b9)
![image](https://github.com/user-attachments/assets/aa1b60a9-30e2-4b04-92d2-9b3ba1d760b1)
![image](https://github.com/user-attachments/assets/cb5de5b3-ff92-4ed4-aedf-ca0ef4d42850)


## License

This project is licensed under the **GNU General Public License v3.0** (GPL-3.0). 

[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

### Key Permissions:
- ✅ Commercial use
- ✅ Modification
- ✅ Distribution
- ✅ Patent use
- ✅ Private use

### Main Requirements:
- ❗ License and copyright notice must be included
- ❗ Same license must be used for derivative works
- ❗ State changes made to original code
- ❗ Disclose source code

### Limitations:
- ⚠️ No liability
- ⚠️ No warranty

For full license terms, see [LICENSE](LICENSE) file or read the [GNU GPL v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html) official documentation.
