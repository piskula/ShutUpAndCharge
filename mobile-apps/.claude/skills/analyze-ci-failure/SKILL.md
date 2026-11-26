---
name: analyze-ci-failure
description: Use to check GitHub Actions build status of the mobile app or diagnose why a mobile workflow run failed and propose a fix. Triggered by phrases like "check the latest build", "did CI pass", "why did the build fail", "check the GitHub Actions run", "analyze this workflow run", "check run <id>", "check the build for this PR", "why did the mobile release fail".
---

# Skill: Analyze a GitHub Actions Run and Propose a Fix

> Uses the `gh` CLI (already authenticated) to inspect workflow runs of `piskula/ShutUpAndCharge` on GitHub Actions, isolate the actual failing step from a noisy log, and turn it into a root cause + a proposed fix.

## Workflows in this repo

All workflows live at the repo root (`.github/workflows/`), even though the mobile jobs build `mobile-apps/` (each `run:` step uses `working-directory: mobile-apps`). Shared setup lives in the composite actions `.github/actions/mobile-setup-gradle` (JDK 25 + Gradle for `mobile-apps/`) and `.github/actions/mobile-archive-ios-app` (fetch `GoogleService-Info.plist`, archive + export the ad-hoc `.ipa`, upload dSYMs to Crashlytics).

| File | Trigger | Jobs |
|---|---|---|
| `.github/workflows/mobile-build.yml` | push to `main` / PRs touching `mobile-apps/**` (or the mobile workflow/actions; `*.md` ignored), `workflow_dispatch` | `verify` (spotlessCheck, detekt, unit tests, Android Lint), `build-android` (debug APK), `build-ios` (signed ad-hoc `.ipa` when iOS secrets exist, otherwise unsigned Simulator build) → on `main` only: `publish-latest` (replaces the `mobile-latest` GitHub pre-release), `distribute-android`, `distribute-ios` (Firebase App Distribution, group `internal-testers`) |
| `.github/workflows/mobile-release.yml` | push tag `mobile-vMAJOR.MINOR.PATCH` | `prepare` → `build-android`, `build-ios` → `publish-release`, `distribute-firebase`, `upload-google-play` |
| `.github/workflows/gradle.yml` | backend build — ignores `mobile-apps/**` and the mobile workflows/actions | Not relevant for mobile failures |

## Step 1 — Find the run

| Given | Command |
|---|---|
| "latest build" / "latest run" | `gh run list --workflow mobile-build.yml --branch main --limit 5` |
| Latest release run | `gh run list --workflow mobile-release.yml --limit 5` |
| A run ID or run URL | `gh run view <run-id>` |
| A PR number | `gh pr checks <pr-number>` |
| Only failures | `gh run list --workflow mobile-build.yml --status failure --limit 10` |

## Step 2 — Check job-level status, not just the run-level conclusion

`gh run view <run-id>` prints a ✓/X per job. **Always read this before pulling logs** — a run can be marked `failure` even when the job that matters to you succeeded, and vice versa the job you care about can be masked by a downstream job's recovery logic.

Concretely in `mobile-build.yml`: `publish-latest` has `if: always()` (but also requires `verify` to have succeeded) and downloads each artifact with `continue-on-error: true`, so it only fails if *neither* `build-android` nor `build-ios` produced anything. If `build-ios` fails but `build-android` succeeds, `publish-latest` still publishes a partial `mobile-latest` pre-release — but the overall run is still reported as `failure` because `build-ios` failed. Conversely, if `verify` fails, `publish-latest` and both `distribute-*` jobs are **skipped**, not failed. `distribute-ios` is also skipped (not failed) whenever `build-ios` fell back to the unsigned Simulator build. **Identify the specific job(s) that actually failed**, don't assume the run summary tells the whole story.

## Step 3 — Pull only the failing job's log, then grep for the signal

`gh run view <run-id> --log-failed` dumps every line of every failed step verbatim — runner provisioning info, Node deprecation warnings, Gradle cache upload progress, etc. Reading it top-to-bottom or `tail`-ing it is unreliable; the real error is often buried above generic exit-code lines. Grep for it instead:

```bash
gh run view <run-id> --log-failed | grep -inE "error:|FAILURE:|Execution failed|exception|##\[error\]"
```

Then pull context around the matching line number(s):

```bash
gh run view <run-id> --log-failed | sed -n '<start>,<end>p'
```

To scope to one job (find its ID from Step 2's job list):

```bash
gh run view <run-id> --job <job-id> --log-failed
```

If grep finds nothing useful, the failure may be a plain `BUILD FAILED` with no separate `error:` line — in that case find the last `> Task :...` line before the failure; that's the failing Gradle task. `verify` runs its Gradle tasks with `--continue`, so several tasks can fail in one run — check each `> Task ... FAILED`. When `verify` fails, the `mobile-verify-reports` artifact has the detekt/lint/test reports (`gh run download <run-id> -n mobile-verify-reports`).

## Step 4 — Known failure signatures in this repo

| Symptom in the log | Root cause | Fix |
|---|---|---|
| `verify` fails in `spotlessKotlinCheck` / `spotlessCheck` with a ktlint diff | Unformatted Kotlin/KTS was committed (the pre-commit hook in `mobile-apps/.githooks/pre-commit` only formats staged `mobile-apps/**/*.kt`, and only if hooks are installed) | Use the `fix-spotless-issues` skill |
| `verify` fails in `detekt…SourceSet` / `:app:android:detekt` | A rule in `mobile-apps/detekt.yml` is violated (`failOnSeverity = Error`) | Read the reported rule id and fix the code; reproduce with `./gradlew detektCommonMainSourceSet detektAndroidMainSourceSet detektIosMainSourceSet :app:android:detekt` |
| `verify` fails in `:…:testAndroidHostTest` | A `commonTest` unit test failed | Reproduce with `./gradlew :<module>:testAndroidHostTest`; read the report under `<module>/build/reports/tests/` |
| `verify` fails in `:app:android:lintDebug` | An Android Lint error | Read `app/android/build/reports/lint-results-debug.*` (or the SARIF report) and fix |
| `File google-services.json is missing` / `processDebugGoogleServices` fails, or `apps:sdkconfig` fails with an auth/404 error | The "Fetch Firebase Android config" step couldn't download the config: `FIREBASE_TOKEN` expired/missing or `SUAC_FIREBASE_ANDROID_APP_ID` wrong (Firebase project `shutupandcharge`) | Refresh the secret (`firebase login:ci` → `gh secret set FIREBASE_TOKEN`); don't commit `google-services.json` |
| `error: Unknown iOS simulator arch: 'x86_64'` during `build-ios` / `syncComposeResourcesForIos` | Xcode/Compose Multiplatform resource sync targeting a legacy x86_64 simulator slice not supported on the `macos-15` (Apple Silicon) runner — the Kotlin build only has `iosArm64` and `iosSimulatorArm64` targets | Exclude `x86_64` from simulator archs — check `EXCLUDED_ARCHS` / `ARCHS` in `mobile-apps/app/ios/iosApp.xcodeproj/project.pbxproj`, or narrow the `xcodebuild -destination` in `mobile-build.yml` to `platform=iOS Simulator,arch=arm64` |
| `embedAndSignAppleFrameworkForXcode` fails inside `xcodebuild` | The Kotlin/Native `Shared` framework from `:app:shared` failed to compile — the real Kotlin error is further up in the same log | Find the `e: file://…` line above it and fix that |
| `security: SecKeychainItemImport: …` / `Cannot parse a NULL or zero-length data`, or `error decoding base64 input stream` in "Import signing certificate and provisioning profile" | Broken iOS signing secret (wrong `.p12` encryption or corrupted upload) | See the `manage-ios-signing` skill |
| `No profile for team … matching '<name>' found` / `doesn't include the currently selected device` | The ad-hoc profile in `IOS_ADHOC_PROFILE_BASE64` doesn't match the certificate, bundle id `org.momosi.shutupandcharge`, or has expired | Regenerate the profile — `manage-ios-signing` skill |
| `Unable to download artifact(s): Artifact not found for name: X` in `publish-latest` | An upstream job's build/upload step failed or was skipped, so the artifact was never uploaded — this is a symptom, not the root cause | Diagnose the upstream job (`build-android`/`build-ios`) that should have produced `X`; this step already has `continue-on-error: true`, don't "fix" it by suppressing the error further |
| `failed to run git: fatal: not a git repository` right before a `gh release create`/`gh release delete` step fails | The job has no checkout, so `gh` has no local repo context | The release jobs set `GH_REPO: ${{ github.repository }}` on the `gh` steps — keep it there (or add `actions/checkout`) if editing them |
| `e: file:///…/File.kt:12:5 ...` | Kotlin compiler error, points directly at file:line | Open the file, fix per `mobile-apps/AGENTS.md` conventions |
| `Tag 'X' does not match required mobile-vMAJOR.MINOR.PATCH format` or `MINOR and PATCH must be less than 100` (`mobile-release.yml`) | Git tag doesn't satisfy the `Parse version from tag` step's regex/range check | Re-tag with a valid `mobile-vMAJOR.MINOR.PATCH` where MINOR and PATCH are each `< 100` — see the `release-app` skill |
| `mv: cannot stat '…/apk/release/android-release.apk'` in the release `build-android` job | Gradle produced `android-release-unsigned.apk` because one of the `ANDROID_KEYSTORE_*`/`ANDROID_KEY_*` secrets is blank — `suac.android.signing` only wires the release signing config when all four are set | Set the missing secret(s) — see the `release-app` skill |
| `Execution failed for task ':…:compileAndroidMain'` / `':…:compileKotlinIosArm64'` or similar per-module compile task | Compile error scoped to one module | Narrow reproduction locally: `./gradlew :module:path:<same task>` from `mobile-apps/` |

If a failure doesn't match this table, treat the grepped `error:`/`FAILURE:` line as authoritative and reason from there — don't guess.

## Step 5 — Reproduce and propose a fix

1. Where possible, reproduce locally with the same Gradle task the workflow ran (see `run:` steps in the workflow YAML), from `mobile-apps/` (`./gradlew …`, or `.\gradlew.bat …` on Windows) before proposing a fix — this repo's mobile CI failures are almost always reproducible with `./gradlew <same task>`. Local builds need `app/android/google-services.json` — see the `setup-local-tools` skill.
2. State the root cause in one sentence, citing the exact log line.
3. Propose a minimal diff:
   - Workflow YAML bug → edit `.github/workflows/mobile-*.yml` or `.github/actions/mobile-*/action.yml` directly.
   - Application/build code bug → fix the code per `mobile-apps/AGENTS.md` conventions and run `fix-spotless-issues` if it's formatting-related. Never change the backend build (`module-api`/`module-server`/`module-ui`) to fix a mobile failure.
4. **Never "fix" a failure by hiding it** — no removing the failing step, no `continue-on-error: true`, no `|| true`, no disabling a check — unless the user explicitly asks for that. The goal is root cause, not a green checkmark.

## Verification

- [ ] Identified the specific failing job (not just "the run failed")
- [ ] Found the actual `error:`/`FAILURE:` line, not just generic exit-code noise
- [ ] Root cause stated as one sentence with a cited log line
- [ ] Fix reproduces/resolves locally where the failure type allows it (e.g. a Gradle task)
- [ ] Fix addresses the cause, not just silences the check
