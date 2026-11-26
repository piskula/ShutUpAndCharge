---
name: manage-ios-signing
description: Use when adding/removing an iOS tester device, regenerating the ad-hoc provisioning profile, or renewing the iOS distribution certificate for Firebase App Distribution of the mobile app. Triggered by phrases like "add an iOS tester", "register a new device", "regenerate the provisioning profile", "renew the iOS certificate", "update IOS_ADHOC_PROFILE_BASE64", "iOS signing secrets", "set up iOS signing".
---

# Skill: Manage iOS Ad-Hoc Signing (Certificate, Provisioning Profile, GitHub Secrets)

> `build-ios` in both `.github/workflows/mobile-build.yml` and `.github/workflows/mobile-release.yml` (at the repo root) sign real ad-hoc `.ipa`s for the single (prod) app `org.momosi.shutupandcharge` using one Apple Distribution certificate + one ad-hoc provisioning profile, stored as GitHub Secrets. There are no other environments, no App Store / TestFlight profile and no App Store Connect API key. This skill covers the operations that come up again: first-time setup, adding a tester's device, and renewing the certificate.

## How CI uses the secrets

- Both workflows' `build-ios` first check that `IOS_TEAM_ID`, `IOS_DISTRIBUTION_CERTIFICATE_BASE64`, `IOS_DISTRIBUTION_CERTIFICATE_PASSWORD` and `IOS_ADHOC_PROFILE_BASE64` are all set. If any is missing, `mobile-build.yml` falls back to an unsigned iOS Simulator build (and `distribute-ios` is skipped), and `mobile-release.yml` skips the iOS release (the GitHub Release and Firebase distribution go out Android-only) — both with a `::warning::`, not a failure.
- With all four set, the job imports the `.p12` into a temporary keychain, installs the profile, reads its `Name` from the decoded plist, and the composite action `.github/actions/mobile-archive-ios-app` archives `mobile-apps/app/ios/iosApp.xcworkspace` (scheme `iosApp`) with `CODE_SIGN_IDENTITY="Apple Distribution"`, `PROVISIONING_PROFILE_SPECIFIER=<profile name>`, exports with `method = ad-hoc` for bundle id `org.momosi.shutupandcharge`, and uploads dSYMs to Crashlytics.
- The signed `.ipa` goes to Firebase App Distribution (`SUAC_FIREBASE_IOS_APP_ID`, project `shutupandcharge`, group `internal-testers`). Only devices listed in the ad-hoc profile can install it.

## Looking up current values

This file intentionally does **not** hardcode the Apple Team ID, Firebase App ID, or provisioning profile name — `piskula/ShutUpAndCharge` is a public repo, and those specifics belong in the agent's private memory or the Apple/Firebase consoles, not in a committed file anyone can read. To find them:

- Team ID / cert expiry: Apple Developer portal → Account → Membership; or `security find-certificate` on a machine with the cert imported.
- Firebase iOS App ID: `firebase apps:list --project shutupandcharge` (bundle ID `org.momosi.shutupandcharge` — fixed and non-sensitive, it's embedded in the shipped app anyway).
- Which/how many devices the current ad-hoc profile covers: Apple Developer portal → Profiles → open the profile, or decode it locally (`security cms -D -i profile.mobileprovision`, never in CI logs — see "Public repo" section below).
- Existing secret names (not values): `gh secret list --repo piskula/ShutUpAndCharge`.

Ad-hoc profiles cap at 100 devices per device type per membership year; every device added means editing the profile and re-downloading it.

## The secrets involved

| Secret | Changes when... |
|---|---|
| `IOS_TEAM_ID` | Never (unless you switch Apple Developer accounts) |
| `SUAC_FIREBASE_IOS_APP_ID` | Never (unless the Firebase iOS app is re-registered) |
| `FIREBASE_TOKEN` | Only when the Firebase CLI token is revoked/expired (`firebase login:ci`) — shared with Android |
| `IOS_DISTRIBUTION_CERTIFICATE_BASE64` | Certificate renewal only (yearly) |
| `IOS_DISTRIBUTION_CERTIFICATE_PASSWORD` | Same time as the cert above |
| `IOS_ADHOC_PROFILE_BASE64` | **Every time a tester device is added/removed**, and after a certificate renewal |

Adding a tester only touches `IOS_ADHOC_PROFILE_BASE64`. You do **not** need to regenerate the certificate to add a device.

## First-time setup

1. Apple Developer portal → **Identifiers → +** → App ID with the explicit bundle ID `org.momosi.shutupandcharge` (no extra capabilities are needed — the app uses no push notifications or associated domains).
2. Create the Apple Distribution certificate (see "Renewing the certificate" below — same steps).
3. Register the testers' devices and create the ad-hoc profile (see "Adding a new tester device", steps 1–3).
4. Upload all four `IOS_*` secrets plus `SUAC_FIREBASE_IOS_APP_ID` (see "Uploading secrets safely"), then trigger a run (see the end of that section) and check that `build-ios` produced `shut-up-and-charge.ipa` instead of the Simulator fallback.

## Adding a new tester device

1. Get the tester's iPhone UDID. On Windows: connect the iPhone via USB, open iTunes, go to the device summary page, click the serial number once (cycles to show the UDID), right-click → Copy. On a Mac: Xcode → Window → Devices and Simulators. Alternatively, inviting the tester through Firebase App Distribution lets them register their device from the invite, after which Firebase shows the UDID in the console.
2. Apple Developer portal → **Certificates, Identifiers & Profiles → Devices → +** → register the UDID.
3. **Profiles** → edit the existing ad-hoc profile for App ID `org.momosi.shutupandcharge` (or create a new one: **+** → **Ad Hoc** → the App ID → the Apple Distribution cert → select **all** devices that should be covered, including the new one) → download the `.mobileprovision`.
4. Also invite the tester's email to the Firebase App Distribution tester group `internal-testers` (project-level, shared by Android and iOS; both the debug builds from `main` and the tagged releases go to it):
   ```bash
   firebase appdistribution:testers:add <email> --group-alias internal-testers --project shutupandcharge
   ```
5. Base64-encode and update `IOS_ADHOC_PROFILE_BASE64` — **see "Uploading secrets safely" below.**

Existing builds don't pick up the new device — the tester can only install builds signed after the profile secret was updated.

## Renewing the certificate (only when it expires, ~yearly)

1. Generate a CSR (works fine on Windows via Git Bash, no Mac needed — the leading-`/subj` MSYS path-mangling gotcha requires `MSYS_NO_PATHCONV=1`):
   ```bash
   openssl genrsa -out ios_distribution.key 2048
   MSYS_NO_PATHCONV=1 openssl req -new -key ios_distribution.key -out ios_distribution.csr \
     -subj "/emailAddress=you@example.com/CN=Your Name/C=XX"
   ```
2. Apple Developer portal → **Certificates → +** → **Apple Distribution** → upload the `.csr` → download the resulting `.cer`.
3. Convert and bundle into a `.p12`. OpenSSL 3.x defaults to AES-256/SHA-256 PKCS12 encryption, which macOS's Keychain Services `security import` (used in CI, `macos-15` runners) **cannot parse** — it fails with the cryptic `Cannot parse a NULL or zero-length data`, even though the file decodes byte-for-byte correctly from the GitHub secret. You must force the legacy RC2/3DES + SHA1 format explicitly:
   ```bash
   openssl x509 -inform DER -in distribution.cer -out distribution.pem

   openssl pkcs12 -export -legacy \
     -inkey ios_distribution.key \
     -in distribution.pem \
     -out ios_distribution.p12 \
     -name "iOS Distribution" \
     -certpbe PBE-SHA1-3DES \
     -keypbe PBE-SHA1-3DES \
     -macalg sha1 \
     -password "pass:<a-generated-password>"
   ```
   Sanity-check before uploading: `openssl pkcs12 -info -in ios_distribution.p12 -passin "pass:..." -noout` should show `pbeWithSHA1And3-KeyTripleDES-CBC` for both bags and `MAC: sha1` — if it shows AES, the `-legacy`/`-certpbe`/`-keypbe` flags didn't take (check the OpenSSL install has the legacy provider available).
4. Base64-encode and update **both** `IOS_DISTRIBUTION_CERTIFICATE_BASE64` and `IOS_DISTRIBUTION_CERTIFICATE_PASSWORD`.
5. The ad-hoc profile references a specific certificate, so regenerate it against the new certificate too (see "Adding a new tester device", step 3) and update `IOS_ADHOC_PROFILE_BASE64`.

Keep `ios_distribution.key`, the `.p12` and its password out of the repository (store them in a password manager) and delete the local working copies once the secrets are uploaded.

## Uploading secrets safely

**Never** pipe a secret value through PowerShell to `gh secret set` (`Get-Content -Raw | gh secret set NAME`) — Windows PowerShell 5.1's text pipeline to a native process re-encodes the string, which can silently corrupt a large base64 payload into invalid data (decoding then fails in CI with `error decoding base64 input stream`) even though the source file is correct. Always use Bash with **raw file redirection**, which passes bytes through untouched:

```bash
base64 -w0 adhoc.mobileprovision > adhoc.mobileprovision.base64.txt
gh secret set IOS_ADHOC_PROFILE_BASE64 --repo piskula/ShutUpAndCharge < adhoc.mobileprovision.base64.txt

base64 -w0 ios_distribution.p12 > ios_distribution.p12.base64.txt
gh secret set IOS_DISTRIBUTION_CERTIFICATE_BASE64 --repo piskula/ShutUpAndCharge < ios_distribution.p12.base64.txt
```

Watch trailing newlines too: `echo` and some `Get-Content` paths append one, which is invisible but changes the exact byte sequence gh uploads — a password with a trailing newline no longer matches the `.p12`. When piping a password/short value, use `printf '%s'` instead of `echo`, or strip the trailing newline explicitly:
```bash
printf '%s' "$(cat p12_password.txt)" | gh secret set IOS_DISTRIBUTION_CERTIFICATE_PASSWORD --repo piskula/ShutUpAndCharge
```

Base64-encode with GNU `base64 -w0 file > file.b64` (single line, no wrapping) — that's fine for encoding. **Decoding happens in CI on macOS**, where `/usr/bin/base64` is the BSD variant and needs `-D`, not GNU's `--decode` long flag (both `mobile-build.yml` and `mobile-release.yml` already use `-D` — don't regress this if editing those files).

After updating any secret, verify with a fresh CI run before assuming it worked — `gh workflow run mobile-build.yml --repo piskula/ShutUpAndCharge --ref main` (uses the `workflow_dispatch` trigger, no throwaway commit needed) and watch the `build-ios` job: it must not print the "iOS signing secrets are not configured" warning and must upload the `mobile-ios-ipa` artifact.

## Public repo — logging discipline

`piskula/ShutUpAndCharge` is a **public** repository, so Actions logs are visible to anyone, not just collaborators. If you add debug output while troubleshooting this pipeline:
- Byte counts, `file` type output, variable *lengths* (`${#VAR}`), and exit codes are safe.
- **Never** dump decoded certificate/profile file *content* (e.g. `head`/`cat` on the decoded `.p12` or `.mobileprovision`/plist) — the profile plist contains the testers' device UDIDs and the profile display name (often a real name), and dumping it leaks that publicly. `security cms -D -i profile.mobileprovision` output is exactly this risk — the workflows write it to a file in `$RUNNER_TEMP` and only read `UUID`/`Name` from it; keep it that way.
- GitHub auto-masks log text that exactly matches a registered secret value, but that only protects the literal secret string — it does nothing for derived content like a decoded plist's fields. Don't rely on masking; just don't print that content in the first place.
- Remove any temporary `DEBUG` lines once a fix is confirmed working — don't leave diagnostic scaffolding in the workflow permanently.
