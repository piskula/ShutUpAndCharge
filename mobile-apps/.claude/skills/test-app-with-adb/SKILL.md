---
name: test-app-with-adb
description: Use when driving the running Android app via adb to verify a UI change — navigating screens, tapping specific elements, filling text fields, or confirming a screen looks/behaves correctly after an edit. Triggered by phrases like "test this in the app", "navigate to the transactions screen", "open the admin tab", "check this looks right on device", "tap the start charging button", "verify the UI change works".
---

# Skill: Drive the App via adb Using Compose Test Tags

> Every interactive element in the mobile app carries a `Modifier.testTag(...)`, and the app root
> calls `markTestTagsAsResourceId()` (Android: `testTagsAsResourceId = true`) — so
> `adb shell uiautomator dump` exposes each tag as a **`resource-id`** you can grep for and tap by
> bounds, instead of guessing coordinates from a screenshot or fuzzy-matching visible text (which
> breaks under localization — the app ships English and Slovak — dynamic content, or layout shifts).

## Why this matters

Compose semantics nodes have **no stable identifier** in a `uiautomator` dump by default —
`Modifier.testTag()` alone does not surface in `resource-id`; it only becomes visible there once
`testTagsAsResourceId = true` is set on an ancestor semantics node. That's already done once, for
the whole app, in `app/shared/src/commonMain/kotlin/org/momosi/shutupandcharge/app/SuacApp.kt` on
the root `Scaffold` (via `markTestTagsAsResourceId()` from
`core/ui-library/.../TestTagsAsResourceId.kt`, a no-op on iOS). You never need to set it again — just
make sure any new interactive element gets a `testTag`.

## ⚠️ This is the production app

There is only one environment: the debug build talks to the real backend `https://charge.momosi.org/`
and the real shared EV charger. **Never tap `charger_start_button`, `charger_stop_button` or
`charger_current_<amps>`, and never confirm admin actions (top-up, verified flag, RFID chip,
`admin_sync_button`) unless the user explicitly asks for it** — they start/stop a real charging
session, change the real charging current, or change real users' balances and accounts. Read-only
navigation, the public Welcome screen, refresh, filters, export and the Profile dialogs are safe.

## Step 1 — Build, install, launch

Run from `mobile-apps/` (needs `app/android/google-services.json` — see the `setup-local-tools` skill):

```bash
./gradlew :app:android:assembleDebug
adb install -r app/android/build/outputs/apk/debug/android-debug.apk
adb shell monkey -p org.momosi.shutupandcharge -c android.intent.category.LAUNCHER 1
```

If the device is locked: `adb shell input keyevent 82 && adb shell wm dismiss-keyguard`.

Signing in opens the Keycloak login (`sso.momosi.org`) in a browser/Custom Tab — that page is not
part of the app, has no test tags, and needs real credentials. Ask the user to sign in on the device
rather than typing credentials yourself; the session survives reinstalls with `-r`. Admin-only UI
(the Admin tab) requires an account with the `MOMO_ADMIN` realm role.

**Windows/Git Bash gotcha:** Git Bash rewrites leading `/sdcard/...` paths to a Windows path before
they reach `adb`, breaking `uiautomator dump /sdcard/window_dump.xml`. Either export
`MSYS_NO_PATHCONV=1` for the session, or double the leading slash (`//sdcard/window_dump.xml`).

## Step 2 — Dump the hierarchy and read tags

```bash
adb shell uiautomator dump //sdcard/window_dump.xml
adb pull //sdcard/window_dump.xml ./window_dump.xml
grep -o 'resource-id="[a-zA-Z][^"]*"' window_dump.xml   # lists every tagged element on screen
```

Each match is a `testTag` value from the table below (or one you just added). To tap one, grab its
`bounds="[x1,y1][x2,y2]"` from the same node and tap the center:

```bash
grep -o 'resource-id="nav_bar_item_TransactionsNavKey"[^/]*bounds="[^"]*"' window_dump.xml
adb shell input tap <center_x> <center_y>
```

To type into a focused text field: `adb shell input text "hello"` (tap the field's tag first to
focus it — e.g. `input_dialog_field`).

For a quick visual check instead of/alongside the hierarchy dump:
`adb exec-out screencap -p > screen.png`, then view it. Delete `window_dump.xml`/`screen.png` when
done — don't commit them.

## Naming convention (follow this for any new tag)

`snake_case`, scoped by screen: `<screen>_<element>`, e.g. `transactions_export_button`. List
items and dynamic collections append a stable id, not an index into a mutable list:
`admin_user_${user.id}`. Reusable `:core:ui-library` components that are structurally singular (the
toolbar back arrow, a dialog's buttons, the charger card's controls) get one fixed tag baked into the
component itself, so every screen that uses them gets the tag for free — don't re-tag these per
screen.

## Tag catalog

**Global, from `:core:ui-library` (present on every screen that uses the component):**

| Tag | Component | Notes |
|---|---|---|
| `nav_bar_item_<NavKeySimpleName>` | `NavigationBar` | Bottom navigation, visible on the main tabs only: `nav_bar_item_DashboardNavKey`, `_TransactionsNavKey`, `_AdminNavKey` (admins only), `_ProfileNavKey` |
| `charger_refresh_button`, `charger_status_pill` | `ChargerStatusCard` | Welcome and Dashboard |
| `charger_start_button` / `charger_stop_button`, `charger_current_<amps>` | `ChargerStatusCard` | Signed-in only; start/stop shown depending on charger state, current chips only while charging. **Real charger — see warning above** |
| `charger_check_connection_button` | `ChargerStatusCard` | Signed-in, while charging can't start yet (Free, Offline, Error, Unknown); only refreshes the status, safe to tap |
| `transaction_stats_<transactionId>` | `TransactionRow` | Only on charging sessions with a stats link; opens the session chart in the browser |
| `chart_month_<index>` | `StackedBarChart` | Selects that month (0 = oldest) |
| `filter_chip_<value>` | `FilterChipRow` | `<value>` is the item's `toString()`, e.g. `filter_chip_Charging` |
| `confirmation_dialog_confirm_button` / `confirmation_dialog_dismiss_button` | `ConfirmationDialog` | Only one can be on screen at a time |
| `info_dialog_dismiss_button` | `InfoDialog` | Profile's version, language and restart-notice dialogs |
| `input_dialog_field`, `input_dialog_confirm_button` / `input_dialog_dismiss_button` | `InputDialog` | Admin top-up and RFID chip dialogs |
| `toolbar_back_button` | `Toolbar` | Monthly usage |

**Welcome** (signed out): `sign_in_button`, plus the charger card's `charger_refresh_button` /
`charger_status_pill` (no start/stop while signed out)

**Dashboard:** `dashboard_list`, `dashboard_balance`, `dashboard_show_all_button` (opens Transactions), plus
the charger card tags

**Transactions:** `transactions_export_button` (Excel export → system share sheet), `transactions_list`;
admins additionally get the filters `transactions_only_mine_switch` and `filter_chip_All` / `_TopUps` /
`_Charging`, and `transactions_monthly_usage` (opens Monthly usage)

**Monthly usage** (admins, from Transactions): `monthly_usage_content`, `monthly_usage_chart`,
`filter_chip_Total` / `_BySeries`, `chart_month_<index>`, `monthly_usage_breakdown`, `toolbar_back_button`

**Admin** (admins only): `filter_chip_Users` / `_Pending`, `admin_users_list`,
`admin_user_<accountId>` (opens the user bottom sheet), `admin_action_top_up`,
`admin_action_toggle_verified` (→ `confirmation_dialog_*`), `admin_action_rfid`,
`admin_action_copy_id`, `admin_pending_list`, `admin_sync_button`

**Profile:** `profile_user_card`, `profile_language_row`, `profile_language_en` /
`profile_language_sk`, `profile_version_row`, `profile_sign_out_button`, `info_dialog_dismiss_button`

## Warnings

- `profile_sign_out_button` really signs out (Keycloak logout); signing back in needs the user's
  credentials. Avoid it unless the task is about sign-out.
- Switching language (`profile_language_*`) may show a restart notice; it changes the app's
  persisted language — switch back when done.
- The tag catalog above reflects what exists as of this skill's writing. If a grep for an expected
  tag comes up empty, don't assume the convention broke — re-check the source file first, since
  screens do get restructured.

## Adding a tag to a new interactive element

Add `Modifier.testTag("<screen>_<element>")` to the element's own modifier chain (buttons, text
fields, clickable rows/boxes — not the static text/icon inside them). No other wiring needed; the
root-level `markTestTagsAsResourceId()` already covers the whole tree. Update the catalog above
in the same change.
