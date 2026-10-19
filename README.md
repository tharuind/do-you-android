# Do You — Android Productivity App (Prototype v0.1)

**Package:** `com.doyou.app`  
**Status:** Android Studio source project, **not a precompiled APK**. This environment had no Android SDK, Gradle or external network access, so the APK could not be built or installed-tested here. Do not treat this source as production-ready.

Original brand/design, built as an offline HTML/CSS/JavaScript interface hosted inside a Java Android WebView shell. No external JS packages, trackers, Internet permission or server connection are required for current offline features. Notifications and backup save dialogs use small native Android bridges.

## ✅ Features actually implemented in this version

- Mobile dashboard with daily cards and lists (Inbox, Today, Upcoming, Completed, All Tasks)
- Task CRUD, priority, tags, status, lists, scheduled date, start date, deadline, estimated duration, notes, checklist editing/completion
- Simple recurring tasks: daily, weekdays, weekly, monthly, yearly, every N days; generated following occurrence on completion
- Reminder time offset and native Android approximate reminder notification; re-registers on app open and boot
- Calendar month view and day task listing
- Kanban To Do/Doing/Done with status selector
- Eisenhower Matrix calculated using priority and today's due date
- List/project creation/editing and simple progress
- Habits with goals, check-ins and streak counts
- Focus Pomodoro timer, break, stopwatch, linked tasks, focus history
- Countdown events with yearly/monthly repeat display
- Text notes, goals with milestones/progress
- Task-completion reports, focus totals, habit statistics
- Search, priority/date/title sorting, local task filters
- Rule-based planner and on-device automation conditions for tasks
- Light/dark themes, accent colors, compact display, calendar week start, timer lengths
- Local persistent data, full JSON export/restore, task-only CSV export, recently deleted task recovery
- Offline operation with no account, no Internet requirement

## 🚧 Not yet built (do not describe as implemented)

These planned capabilities require a substantial further implementation: true AI assistant / automatic intelligent calendar scheduling; account sign-in, cloud synchronization, shared collaboration, conflict resolution; automatic cloud/email backups, OAuth/Gmail, external calendars, Google sync; encryption at rest and encrypted backup archives; rich-text document editor, file attachments and audio recording; device widgets, wearables, precise reliable alarm scheduling, location reminders; advanced custom recurrence exception model / iCalendar RRULE; full Timeline/Gantt, sophisticated Kanban drag-and-drop; dependency graph, smart saved filter builder, all TickTick Premium options; real analytics dashboards and full task template library. This v0.1 is a working offline-first **foundation**, not the complete TickTick parity app.

## Android Studio build — Windows instructions (සිංහල)

1. ZIP එක extract කරන්න. `do-you-android` folder එක ඇතුළට යන්න.
2. **Android Studio** install කරලා open කරන්න. `Open` → `do-you-android` folder එක තෝරන්න. (Project root එකේ `settings.gradle` තියෙන folder එක.)
3. Android Studio එක Android SDK Platform 35, Build Tools 35.0.0 සහ Gradle 8.9 download කරනවා නම් ඒවා install/allow කරන්න. Java/JDK 17+ භාවිතා කරන්න.
4. **Gradle Sync** සම්පූර්ණ කරන්න. (ZIP එකේ gradle-wrapper.jar නැති නිසා IDE එකේ local Gradle 8.9 configure කරන්න, නැත්නම් පහත GitHub Actions workflow එක භාවිතා කරන්න.)
5. Top menu → `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`.
6. Build success වුණොත් APK: `app/build/outputs/apk/debug/app-debug.apk`.
7. APK එක Android phone එකට copy කරලා install කරන්න. Android එකේ APK sideload/install permissions අවශ්‍ය විය හැකියි.

**Alternative, easiest reproducible APK build:** GitHub repository එකකට project files upload කරන්න (`.github` folder එක ඇතුළුව). Repository → `Actions` → `Build Do You Android APK` → `Run workflow`. Build success වුණාම workflow run එකේ `Artifacts` යටතේ **DoYou-debug-APK** download කරලා unzip කරන්න. එහි `app-debug.apk` තියෙනවා. GitHub Actions run requires GitHub access/network and may be subject to account usage limits.

## Preview without Android Studio

`app/src/main/assets/index.html` open in Chrome/Edge, or serve the `assets` folder over local HTTP if your browser restricts file storage. The offline interface works in browser too; native Android notifications and Android's SAF save dialog do not work in this preview. To preview with a local web server:

```powershell
cd app\src\main\assets
python -m http.server 8080
```

Open `http://localhost:8080`. Local-storage data is tied to the address/origin; a file-origin preview and Android WebView do **not** automatically share data.

## Security and data warnings

- Storage is currently **WebView localStorage** (plain application-private, not a production database). Do not store passwords or highly sensitive information in this prototype.
- Always export and test a JSON backup before clearing browser data, reinstalling, deleting the app, or resetting its data.
- CSV export only has task fields; JSON contains application records and preferences. Neither is encrypted.
- Notification scheduling uses `AlarmManager.setAndAllowWhileIdle` (inexact). Android battery optimization, Doze, and permission restrictions can delay or block alerts. **Don't rely on this prototype for safety-critical reminders**.
- The app does not contain the `INTERNET` permission and does not transmit data, but a full app security review and device testing have not been completed.
- The current feature automation is evaluated on app open/data changes, **not while the app is closed**.
- Project has a debug APK CI workflow. Debug builds are intended for development/testing, not Play Store release.

## Tests

`node --check app/src/main/assets/app.js`  
`node docs/test_logic.js`

A JavaScript render smoke test for all major views was performed here; real Android Studio Gradle build and on-device testing still need to happen.
