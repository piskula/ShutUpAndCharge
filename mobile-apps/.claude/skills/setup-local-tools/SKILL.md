---
name: setup-local-tools
description: Use when setting up a new machine for the Shut Up And Charge mobile app, checking whether required CLI tools (JDK, Android SDK, Xcode, CocoaPods, gh, Node, firebase-tools) are installed and authenticated, or diagnosing why an agent can't reach Firebase via MCP. Triggered by phrases like "set up my environment", "what do I need to install", "onboard me on this repo", "check my dev tools", "set up the firebase cli", "set up gh cli", "why can't you access firebase".
---

# Skill: Set Up Local Tools for the SUAC Mobile App + AI Agents

> The recommended, from-scratch local setup for building the mobile app in `mobile-apps/` and
> letting an AI coding agent work in it at full capability (`gh`, Gradle, Xcode, optionally the
> Firebase MCP server). Always install the **latest stable** release of each tool unless a specific
> minimum is called out below — don't assume whatever happens to already be on the machine is
> adequate; verify with the check command.
>
> `mobile-apps/` is its own standalone Gradle build (own `settings.gradle.kts`, `gradlew`,
> `gradle/libs.versions.toml` and `build-logic/`). The backend at the repo root
> (`module-api`/`module-server`/`module-ui`) is a separate build with its own tooling (PostgreSQL,
> Node for the Angular UI) — none of that is needed for mobile work.
>
> There is no GitHub MCP server here — `gh` alone already covers everything the
> `analyze-ci-failure` and `release-app` skills need (inspecting CI runs, PRs, cutting releases).

## What the mobile app needs

| Tool | Version | Needed for | Check | Platform |
|---|---|---|---|---|
| JDK | 25 (Temurin/Adoptium or JetBrains Runtime; CI uses `jetbrains` 25) — plus a JDK 21 for `build-logic`'s `jvmToolchain(21)` | Gradle toolchain — `jvmToolchain(25)` in `mobile-apps/build-logic/convention/.../LibraryPlugin.kt` | `java -version` | all |
| Android SDK | cmdline-tools + platform 37 + build-tools (latest) | `./gradlew :app:android:assembleDebug` (from `mobile-apps/`), Android Studio | Android SDK path env var set and exists (`ANDROID_HOME` on macOS/Linux, `%LOCALAPPDATA%\Android\Sdk` on Windows) | all |
| Xcode | latest stable from the App Store (15+ minimum) + CLT | Building `mobile-apps/app/ios` (`iosApp.xcworkspace`) | `xcode-select -p` | **macOS only** — iOS can't be built elsewhere |
| CocoaPods | latest | Linking FirebaseCore + FirebaseCrashlytics into the iOS app (`mobile-apps/app/ios/Podfile`) | `pod --version` | **macOS only** |
| `gh` (GitHub CLI), logged in | latest | Agents running `gh run list`/`gh release create` etc. directly (`analyze-ci-failure`, `release-app` skills) | `gh auth status` | all |
| Node.js | current LTS (20+) | `npx firebase-tools` (config download, App Distribution, optional Firebase MCP server) — an old Node breaks `npx` resolution for modern packages | `node --version` | all |
| `firebase-tools` CLI, logged in | latest | Downloading the Firebase config files, App Distribution tester management, Crashlytics analysis | `firebase --version` and `firebase login:list` | all |

## Step 1 — Install

Pick the section for your OS. Everything except Xcode/CocoaPods/iOS builds applies to macOS, Linux,
and Windows equally.

### macOS (Homebrew)

```bash
brew install --cask temurin@25 temurin@21   # JDK 25 (+ 21 for build-logic)
brew install --cask android-commandlinetools   # or install SDK via Android Studio's SDK Manager
xcode-select --install                  # Xcode itself: install from the App Store first
brew install cocoapods
brew install gh && gh auth login
brew install node                       # current LTS
npm install -g firebase-tools@latest && firebase login
```

### Linux (Debian/Ubuntu — adapt package manager as needed)

```bash
# JDK 25 (+ 21 for build-logic)
sudo apt update && sudo apt install -y openjdk-25-jdk openjdk-21-jdk
# or use sdkman: sdk install java 25-tem && sdk install java 21-tem

# Android SDK — download cmdline-tools from developer.android.com/studio#command-tools,
# unzip, then: sdkmanager "platform-tools" "platforms;android-37" "build-tools;37.0.0"

# GitHub CLI
type -p curl >/dev/null && curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg \
  | sudo dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" \
  | sudo tee /etc/apt/sources.list.d/github-cli.list > /dev/null
sudo apt update && sudo apt install -y gh
gh auth login

# Node.js (current LTS) — via nvm avoids fighting distro package versions
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.1/install.sh | bash
nvm install --lts

# Firebase CLI
npm install -g firebase-tools@latest && firebase login
```

### Windows (PowerShell, winget)

```powershell
winget install EclipseAdoptium.Temurin.25.JDK   # JDK 25
winget install EclipseAdoptium.Temurin.21.JDK   # JDK 21 for build-logic
# Android SDK — install via Android Studio's SDK Manager (simplest on Windows)
winget install GitHub.cli
gh auth login
winget install OpenJS.NodeJS.LTS                # current LTS
npm install -g firebase-tools@latest
firebase login
```

Use `.\gradlew.bat` instead of `./gradlew` in PowerShell (or run `./gradlew` from Git Bash).

Set `ANDROID_HOME` as a persistent user environment variable if not already set (Android Studio's
SDK Manager installs the SDK but doesn't always set this):

```powershell
[System.Environment]::SetEnvironmentVariable("ANDROID_HOME", "$env:LOCALAPPDATA\Android\Sdk", "User")
```

`gh auth login`'s interactive arrow-key menu needs a real TTY — if running through an agent's
non-interactive shell pass-through, use the flag form instead so it only needs one code paste in
the browser:

```bash
gh auth login --hostname github.com --git-protocol https --web
```

`firebase login` refuses to run at all in a non-interactive shell — run it directly in your own
terminal window, not through an agent's shell pass-through.

### Optional — Firebase MCP server

No `.mcp.json` is committed. To give a Claude Code session Firebase tools (Crashlytics issues, App
Distribution), add a `mobile-apps/.mcp.json` locally (or ask before committing one):

```json
{
  "mcpServers": {
    "firebase": { "command": "npx", "args": ["-y", "firebase-tools@latest", "mcp"] }
  }
}
```

Nothing else to configure per machine beyond Node.js and being logged in via `firebase login`; `npx`
resolves the same way on every platform once Node is installed.

## Step 2 — Verify

Run all of these and confirm each passes before considering the machine ready (skip the
macOS-only ones elsewhere):

```bash
java -version                # 25
gh auth status                # logged in
node --version                # 20+
firebase --version
firebase login:list            # shows an authenticated account
xcode-select -p               # macOS only — points at an installed Xcode
pod --version                 # macOS only
```

- [ ] `cd mobile-apps && ./gradlew :app:android:assembleDebug` succeeds (needs the Firebase config from Step 3)
- [ ] The first Gradle run installed the pre-commit hook (`.git/hooks/pre-commit` contains `suac-mobile-pre-commit`) — see the `fix-spotless-issues` skill
- [ ] (macOS only) after `pod install` in `mobile-apps/app/ios`, `open mobile-apps/app/ios/iosApp.xcworkspace` builds and runs in Xcode
- [ ] (if you added `.mcp.json`) a fresh Claude Code session shows the `firebase` MCP server in its tool list
      (MCP servers load at session start — restart the session after finishing setup)

## Step 3 — Firebase config files for local builds

`mobile-apps/app/android/google-services.json` and `mobile-apps/app/ios/iosApp/GoogleService-Info.plist`
are required for Crashlytics (the only Firebase SDK in the app) to compile/link, but neither is
committed — both are gitignored and fetched fresh in CI on every build via
`npx firebase-tools apps:sdkconfig` (see the "Fetch Firebase Android config" steps in
`.github/workflows/mobile-build.yml` / `mobile-release.yml` and the "Fetch Firebase iOS config" step
in `.github/actions/mobile-archive-ios-app`). A fresh local checkout has neither file, so local builds
need one of:

- **Firebase Console** (no CLI needed): `console.firebase.google.com` → project `shutupandcharge` → ⚙️
  Project settings → General → "Your apps" → download `google-services.json` /
  `GoogleService-Info.plist` for the respective app → place at the paths above.
- **`firebase-tools` CLI** (once logged in per Step 1), from the repo root:
  ```bash
  firebase apps:list --project shutupandcharge   # get the ANDROID/IOS app IDs
  firebase apps:sdkconfig ANDROID <ANDROID_APP_ID> --project shutupandcharge -o mobile-apps/app/android/google-services.json
  firebase apps:sdkconfig IOS <IOS_APP_ID> --project shutupandcharge -o mobile-apps/app/ios/iosApp/GoogleService-Info.plist
  ```

There is a single environment (prod): one Android app and one iOS app, both
`org.momosi.shutupandcharge`, talking to `https://charge.momosi.org/` and Keycloak
`https://sso.momosi.org/realms/momosi/` (client `mobile-client`). There are no build flavors, no
per-flavor config files and no staging Firebase apps.

This is a one-time step per machine — the files don't change unless the Firebase project
reconfigures. Never commit them. iOS additionally needs `pod install` run from `mobile-apps/app/ios`
(CocoaPods) before opening `iosApp.xcworkspace`; Xcode then builds the Kotlin framework itself via
`./gradlew :app:shared:embedAndSignAppleFrameworkForXcode`. Device signing for local runs uses your
own Apple ID/team in Xcode — the ad-hoc distribution signing is CI-only (see the
`manage-ios-signing` skill).

## Gotchas worth knowing

| Gotcha | Why it happens |
|---|---|
| `./gradlew` fails with "No matching toolchains found for requested specification: {languageVersion=25}" (or `=21`) even though a JDK is installed | `mobile-apps/settings.gradle.kts` doesn't apply the `org.gradle.toolchains.foojay-resolver-convention` plugin, so Gradle only discovers JDKs already on the machine — it won't auto-download one. Install the missing JDK, point `JAVA_HOME` at the JDK 25 install, or add the JDK paths to `org.gradle.java.installations.paths` in `~/.gradle/gradle.properties` |
| `./gradlew` run from the repo root builds the backend instead of the app | The repo root has its own (backend) Gradle build — always `cd mobile-apps` first |
| `File google-services.json is missing` | Step 3 wasn't done on this machine |
| Firebase MCP tool list looks sparse | No `firebase.json` exists, so the MCP server has nothing to detect and can only offer project-agnostic tools — pass `--project shutupandcharge` / name the project explicitly in requests |
| A newly edited `.mcp.json` doesn't show new tools in the running session | MCP servers are only loaded at session start — start a new Claude Code session after any `.mcp.json` or auth change |
| A tool installed via `winget`/`brew`/`apt` mid-session isn't found (`command not found` / `not recognized`) | The shell process was started before install and cached the old `PATH`. Open a new terminal/session, or on Windows reload it: `$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")` |
