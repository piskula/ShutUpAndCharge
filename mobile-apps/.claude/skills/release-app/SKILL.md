---
name: release-app
description: Use when cutting a signed release of the mobile app (Android + iOS) or explaining how to release it. Triggered by phrases like "release the app", "release the mobile app", "cut a release", "release android", "publish a new version", "tag a release", "how do I release", "release next patch", "release next minor", "release next major", "bump the version".
---

# Skill: Cut a Signed Mobile Release

> `.github/workflows/mobile-release.yml` (at the repo root) builds a signed release APK + AAB and an ad-hoc `.ipa` of the single (prod) app `org.momosi.shutupandcharge`, publishes them as one GitHub Release, distributes them through Firebase App Distribution and uploads the AAB to the Google Play internal track whenever a `mobile-vMAJOR.MINOR.PATCH` tag is pushed. There is no separate version-bump commit or manifest edit — the tag *is* the version, parsed at build time. There is no TestFlight / App Store upload. The `mobile-` prefix keeps mobile tags apart from any backend tags in the same repo.

## Triggers

- "release the app", "cut a release", "release version X.Y.Z"
- "release next patch" / "release next minor" / "release next major", "bump the version"
- "how do I publish a new Android/iOS build"
- Debugging a failed `mobile-release.yml` run (pair with the `analyze-ci-failure` skill)

## How to cut a release

```bash
git tag mobile-v1.2.3
git push origin mobile-v1.2.3
```

That's the entire release process. Pushing the tag runs:

1. **`prepare`** — parses `mobile-vMAJOR.MINOR.PATCH` from `GITHUB_REF_NAME` (**fails fast** if the tag doesn't match, or if `MINOR`/`PATCH` are ≥ 100), computes `versionCode = MAJOR * 10000 + MINOR * 100 + PATCH` (this is why `MINOR`/`PATCH` are capped under 100 — keeps codes from colliding across versions), and writes release notes from the commits touching `mobile-apps/` since the previous `mobile-v*` tag.
2. **`build-android`** — fetches `google-services.json` with `firebase-tools apps:sdkconfig`, decodes the release keystore from `ANDROID_KEYSTORE_BASE64` and runs `./gradlew :app:android:assembleRelease :app:android:bundleRelease` in `mobile-apps/` with `-PappVersionName`/`-PappVersionCode` from the tag (they flow into `mobile-apps/app/android/build.gradle.kts` via `stringProperty("appVersionName"/"appVersionCode")`) and the signing values as `-PandroidKeystoreFile`/`-PandroidKeystorePassword`/`-PandroidKeyAlias`/`-PandroidKeyPassword` (read by `suac.android.signing`). Outputs are renamed to `shut-up-and-charge-<version>.apk` / `.aab`; the R8 `mapping.txt` is uploaded as a separate artifact.
3. **`build-ios`** (parallel to Android) — archives `iosApp` with the `Release` configuration through `.github/actions/mobile-archive-ios-app` (`MARKETING_VERSION` = version name, `CURRENT_PROJECT_VERSION` = version code), producing the ad-hoc `shut-up-and-charge-<version>.ipa` and uploading dSYMs to Crashlytics. Skipped with a warning when the iOS signing secrets are missing.
4. **`publish-release`** — `gh release create "$GITHUB_REF_NAME"` with all artifacts and the release notes, titled `Mobile <version>`, with `--latest=false` (so it doesn't take the "Latest" badge from backend releases). Requires the Android artifacts; publishes without iOS if `build-ios` skipped signing.
5. **`distribute-firebase`** — distributes the APK (and the `.ipa`, if built) to Firebase App Distribution (`SUAC_FIREBASE_ANDROID_APP_ID` / `SUAC_FIREBASE_IOS_APP_ID`, project `shutupandcharge`), group `internal-testers`.
6. **`upload-google-play`** — uploads the AAB plus R8 mapping to the Google Play **internal** track for package `org.momosi.shutupandcharge` (`status: completed`). Skipped with a warning when `PLAY_SERVICE_ACCOUNT_JSON` is missing. Google Play rejects a `versionCode` it has already seen, so re-running this job for the same tag fails.

Pushes to `main` are separate: `mobile-build.yml` publishes debug builds to the `mobile-latest` GitHub pre-release and to Firebase `internal-testers` — that's not a release.

## Releasing the next patch / minor / major

There is **no** version state anywhere in the repo — no `version.properties`, no manifest entry — the only source of truth is the highest existing `mobile-vMAJOR.MINOR.PATCH` git tag. "Release next patch/minor/major" means: find that tag, bump the requested segment, tag, push. Compute it yourself with git, don't guess or hand-type the next number:

```bash
git fetch --tags --quiet
LATEST=$(git tag --list 'mobile-v*' | grep -E '^mobile-v[0-9]+\.[0-9]+\.[0-9]+$' | sort -V | tail -1)
LATEST=${LATEST:-mobile-v0.0.0}   # no release yet -> next patch/minor/major starts from 0.0.0
IFS='.' read -r MAJOR MINOR PATCH <<< "${LATEST#mobile-v}"

# pick exactly one, matching what was asked for:
PATCH=$((PATCH + 1))                              # next patch
# MINOR=$((MINOR + 1)); PATCH=0                    # next minor
# MAJOR=$((MAJOR + 1)); MINOR=0; PATCH=0            # next major

NEXT="mobile-v${MAJOR}.${MINOR}.${PATCH}"
echo "Latest: $LATEST -> Next: $NEXT"

git tag "$NEXT"
git push origin "$NEXT"
```

`sort -V` (version sort) is what makes this correct across width changes, e.g. `mobile-v1.9.0` sorting before `mobile-v1.10.0`. The `grep -E` filter matters too — it excludes any non-mobile or non-release tags (backend tags, `mobile-latest`) so an unrelated tag can't be mistaken for the latest version. Reject the bump before tagging if the resulting `MINOR`/`PATCH` would hit the `< 100` cap (see Tag rules below) — that can only happen on `next minor` when `MINOR` is already 99, or on `next patch` when `PATCH` is already 99.

## Required secrets

Four Android signing secrets must exist (`gh secret list --repo piskula/ShutUpAndCharge` to confirm). `suac.android.signing` (`mobile-apps/build-logic/convention/.../AndroidSigningPlugin.kt`) only wires the `release` signing config when all four values are non-blank — otherwise Gradle silently produces an unsigned `android-release-unsigned.apk` and the "Collect APK and AAB" step fails because `android-release.apk` doesn't exist:

| Secret | Used for |
|---|---|
| `ANDROID_KEYSTORE_BASE64` | base64-encoded `.keystore`/`.jks` file, decoded to `$RUNNER_TEMP/release.keystore` |
| `ANDROID_KEYSTORE_PASSWORD` | keystore password |
| `ANDROID_KEY_ALIAS` | signing key alias |
| `ANDROID_KEY_PASSWORD` | signing key password |

Also needed: `FIREBASE_TOKEN` and `SUAC_FIREBASE_ANDROID_APP_ID` (config fetch + distribution); for iOS `IOS_TEAM_ID`, `IOS_DISTRIBUTION_CERTIFICATE_BASE64`, `IOS_DISTRIBUTION_CERTIFICATE_PASSWORD`, `IOS_ADHOC_PROFILE_BASE64` and `SUAC_FIREBASE_IOS_APP_ID` (see the `manage-ios-signing` skill); optionally `PLAY_SERVICE_ACCOUNT_JSON` (service account with release permissions for the app in the Play Console). The Play app must already exist with at least one manually uploaded AAB before the API upload works.

## Tag rules

| Rule | Why |
|---|---|
| Must match `^mobile-v([0-9]+)\.([0-9]+)\.([0-9]+)$` exactly | e.g. `mobile-v1.2.3` — not `v1.2.3` (missing `mobile-` prefix, doesn't even trigger the workflow), not `mobile-v1.2` (missing patch) |
| `MINOR` and `PATCH` each `< 100` | Guarantees `versionCode = MAJOR*10000 + MINOR*100 + PATCH` never collides between two different `MAJOR.MINOR.PATCH` combinations |

## If you tag the wrong thing

`gh release create` fails outright if a release for that tag already exists — you can't just re-push the same tag to retry. Delete both the tag and release first:

```bash
gh release delete mobile-v1.2.3 --yes --cleanup-tag   # deletes the GitHub release + remote tag
git tag -d mobile-v1.2.3                              # delete the local tag too (cleanup-tag only removes remote)
```

Then re-tag and push as above. If `upload-google-play` already succeeded for that tag, Google Play keeps the `versionCode` — release the next patch instead of reusing it.

## If the run fails

Use the `analyze-ci-failure` skill against the `mobile-release.yml` run. Known-specific failure modes for this workflow:

| Symptom | Cause |
|---|---|
| `Tag 'X' does not match required mobile-vMAJOR.MINOR.PATCH format` | Wrong segment count or extra suffix (e.g. `mobile-v1.2.3-rc1`) |
| `MINOR and PATCH must be less than 100 to avoid versionCode collisions` | e.g. `mobile-v1.150.0` |
| `mv: cannot stat '…/apk/release/android-release.apk'` in "Collect APK and AAB" | One of the four `ANDROID_KEYSTORE_*`/`ANDROID_KEY_*` secrets is missing, so the build was unsigned |
| Signing-related Gradle error in "Build release APK and AAB" (`Keystore was tampered with`, `Cannot recover key`, alias not found) | Wrong password/alias, or the keystore file is corrupt/mismatched with the alias |
| `gh release create` fails with "release already exists" | The tag was already released once — delete the release + tag first (see above), don't just push again |
| `upload-google-play` fails with `Version code N has already been used` | The tag's `versionCode` was uploaded before — release the next patch |

## Verification

- [ ] Tag matches `mobile-vMAJOR.MINOR.PATCH` with `MINOR`/`PATCH` under 100
- [ ] `gh run view <run-id>` shows `prepare`, `build-android`, `build-ios`, `publish-release`, `distribute-firebase` (and `upload-google-play`, if configured) succeeded
- [ ] `gh release view mobile-vX.Y.Z` shows `shut-up-and-charge-X.Y.Z.apk`, `.aab` and (when iOS signing is configured) `.ipa`
- [ ] (optional) `aapt dump badging shut-up-and-charge-X.Y.Z.apk | head -1` confirms `package: name='org.momosi.shutupandcharge'` and `versionName`/`versionCode` match the tag
