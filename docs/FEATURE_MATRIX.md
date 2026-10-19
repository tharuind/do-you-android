# Do You — Research-to-Implementation Matrix

Scope: Android only. Product identity: Do You (original design). This records the researched TickTick-style feature set and additional ideas, with transparent v0.1 status.

Status: **WORKING** = local prototype behavior implemented; **PARTIAL** = limited/basic implementation; **PLANNED** = no real implementation. UI reference screens alone never imply implementation.

| Capability | v0.1 status | Details / missing work |
|---|---|---|
| Today / Inbox / Upcoming / Completed | WORKING | Offline views and completion |
| Task CRUD, priority, tags, notes, start/end/deadline | WORKING | Start/date fields, sorting, status; no smart calendar conflicts |
| Checklist/subtasks | WORKING | Text-based checklist editing; checks saved |
| Recurring tasks | PARTIAL | Daily, weekday, weekly, monthly, yearly, every N days; no RRULE exceptions or edit-this/following/all |
| Local reminder notification | PARTIAL | Inexact Android native alarms; permission and device behavior not verified |
| Natural-language task capture | PLANNED | No NLP engine yet |
| Task templates | PLANNED | No template builder |
| Batch operations / advanced filters | PARTIAL | Sort, list filters, smart views; no full expression builder |
| Calendar | PARTIAL | Monthly calendar and day list; no full week/day time-blocking UI |
| Kanban | PARTIAL | Columns and status selection; no drag-drop |
| Timeline / Gantt | PLANNED | Needs duration/dependency charts |
| Eisenhower Matrix | WORKING | Rule-based priority/due calculations; not customizable matrix filters |
| Lists / projects | PARTIAL | Create/edit, progress; no nested folder management / permissions |
| Goals / milestones | WORKING | Editable milestone checklist, percentage |
| Habits | PARTIAL | Daily check-ins, count goal, streaks; not advanced histories/schedules/notes |
| Pomodoro / Stopwatch / focus reports | WORKING | Pausing, linked task and history; no white noise or strict mode |
| Countdown | WORKING | Date cards, yearly/monthly repeated display |
| Notes | PARTIAL | Plain text only, no Markdown or attachments |
| Completion and time analytics | PARTIAL | Local statistics and simple charts |
| Smart suggested day plan | PARTIAL | Deterministic rule-based (not AI) |
| If-then automations | PARTIAL | On-device overdue/due today conditions; set priority or tag |
| Themes | WORKING | Light/dark, 6 accents, compact mode |
| Widgets / wearables | PLANNED | No Android app widgets |
| Offline-first persistence | PARTIAL | WebView localStorage; migrate to Room/SQLite for production |
| Trash / restore | PARTIAL | Deleted task restore; not full version history |
| JSON export/restore | WORKING | Plain JSON (not encrypted), replaces local data |
| CSV export | WORKING | Task-only; import CSV not available |
| Cloud sync + backup | PLANNED | Server, authentication, conflict resolution |
| Encrypted email backups | PLANNED | Requires backend, consent, encryption, email delivery |
| Google Calendar / Outlook / Gmail | PLANNED | OAuth integrations and event mapping |
| Sharing / roles / comments | PLANNED | Requires server-backed accounts and permissions |
| AI scheduling / AI assistant / voice | PLANNED | Not a fake AI; requires real inference capability |
| Project dependencies | PLANNED | Requires graph and scheduling |
| Recurrence timezone/DST handling | PARTIAL | Local date arithmetic only, no robust IANA/RRULE support |
| Settings/security/account | PARTIAL | Appearance and local controls; no account, MFA or E2EE |
| Android device / release testing | PLANNED | SDK unavailable in current build environment |

## Future engineering priorities
1. Migrate to Room/SQLite (or equivalent reliable offline DB) and explicit schema migrations.
2. Date/time engine with IANA TZ and RFC 5545 RRULE exceptions.
3. Sync protocol, identity, permissions, conflict rules, operation queue, reliable backup.
4. Full task/calendar interactions and native notifications, accessibility, widgets.
5. Goals, habits, advanced focus, analytics, templates, automations, and AI integrations.
6. Full QA matrix across supported Android API levels and OEM battery restrictions.
