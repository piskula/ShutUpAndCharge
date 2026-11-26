---
name: fix-spotless-issues
description: Use when running Spotless (ktlint + compose-rules-ktlint) or fixing spotlessCheck/spotlessApply failures in the mobile app. Triggered by phrases like "run spotless", "fix spotless", "spotlessCheck failing", "ktlint violation", "format the code", "fix formatting", "spotlessApply".
---

# Skill: Run Spotless and Fix Violations

> Formats and lints all Kotlin/KTS files via the `suac.spotless` convention plugin (`mobile-apps/build-logic/convention/src/main/kotlin/org/momosi/shutupandcharge/SpotlessPlugin.kt`). Applies standard ktlint plus the `compose-rules-ktlint` custom rule set (`io.nlopez.compose.rules:ktlint`, version from `compose-rules-ktlint` in `mobile-apps/gradle/libs.versions.toml`, currently 0.6.8) to every `**/*.kt` / `**/*.kts` file of each mobile module (`suac.library` applies it to every `core/*`, `feature/*` and `app/shared` module; `app/android` applies it directly). Spotless is only configured for the mobile build in `mobile-apps/` — not for the backend build at the repo root.

## Triggers

Load this skill when the task matches **any** of these:
- "run spotless", "format the code", "fix formatting"
- `spotlessCheck` fails locally or in the `verify` job of `.github/workflows/mobile-build.yml`
- A ktlint or compose-rules violation is reported and needs a manual fix

## Commands

Run from inside `mobile-apps/` (its own Gradle build — `./gradlew`, or `.\gradlew.bat` on Windows). Module paths have no extra prefix (e.g. `:core:charger`, `:feature:dashboard:impl`).

```bash
cd mobile-apps
./gradlew spotlessApply    # auto-format all Kotlin/KTS files — run this first, always
./gradlew spotlessCheck    # fails with a diff if any file still needs formatting
```

Scope to one module when iterating quickly:
```bash
./gradlew :core:charger:spotlessApply
./gradlew :feature:dashboard:impl:spotlessCheck
```

If either task fails with a configuration cache error, retry with `--no-configuration-cache`.

**Always run `spotlessApply` before `spotlessCheck`.** Most violations (import order, wildcard imports, indentation, spacing, function-signature multiline) are mechanical and auto-fixed. Only what `spotlessApply` can't rewrite — mostly semantic `compose-rules-ktlint` violations — needs a manual edit. CI's `verify` job runs `spotlessCheck` and fails the build on any diff.

The pre-commit hook `mobile-apps/.githooks/pre-commit` runs `spotlessApply` on the modules of staged `mobile-apps/**/*.kt` files (excluding `build-logic/`) and re-stages them. It is installed into the repo's `.git/hooks/pre-commit` automatically by the `suac.git-hooks` settings plugin on any Gradle invocation in `mobile-apps/` — unless a different (foreign) pre-commit hook already exists there. It doesn't cover `.kts` files, so still run `spotlessApply` after editing build scripts.

## Reading the failure output

`spotlessCheck` / ktlint errors look like:
```
path/to/File.kt:12:1: Wildcard import (cannot be auto-corrected) (standard:no-wildcard-imports)
path/to/File.kt:34:5: Composable functions... (compose:compose-modifier-missing-check)
```
The `(rule-id)` at the end tells you which rule fired — use it to look up the fix below rather than guessing from the message alone.

## Project-specific rule config

From `SpotlessPlugin.kt`'s `editorConfigOverride` (plus `mobile-apps/.editorconfig`) — these are **not** stock ktlint defaults:

| Override | Effect |
|---|---|
| `ktlint_function_signature_rule_force_multiline_when_parameter_count_greater_or_equal_than = 3` | Any function/constructor with ≥ 3 params must be written one-param-per-line |
| `ktlint_compose_compositionlocal-allowlist = disabled` | `compose-rules-ktlint`'s CompositionLocal allowlist check is off — no violation to fix |
| `ktlint_compose_lambda-param-in-effect = disabled` | Lambda-params-in-`LaunchedEffect`/`remember` check is off — no violation to fix |
| `ktlint_function_naming_ignore_when_annotated_with = Composable` | `@Composable` functions are exempt from `camelCase`-function-name rule — `PascalCase` is correct there, not a violation |

## Common violations & fixes

| Rule | Symptom | Fix |
|---|---|---|
| `standard:no-wildcard-imports` | `import androidx.compose.foundation.layout.*` | Import each symbol used, individually |
| `standard:import-ordering` | Imports out of order/grouped wrong | Let `spotlessApply` reorder — do not hand-sort |
| `standard:function-signature` | 3+ param function/constructor on one line | Break into one parameter per line with a trailing comma; `spotlessApply` usually does this automatically |
| `compose:modifier-missing-check` | Root-level `@Composable` emitting UI has no `modifier` param | Add `modifier: Modifier = Modifier` and apply it to the outermost element |
| `compose:modifier-reused-check` | Same `Modifier` instance applied to more than one element | Apply the incoming `modifier` only to the outermost element; give inner elements their own `Modifier` |
| `compose:multiple-emitters-check` | Composable emits more than one top-level UI element | Wrap in a single root container (`Column`, `Box`, etc.) |
| `compose:param-order-check` | Params ordered wrong | Required params first, then `modifier`, then other optional params, trailing-lambda `content`/`onX` last |
| `compose:unstable-collections-check` | `List<T>` / `Map<K,V>` in a stable Composable param or `@Immutable` state | Use `ImmutableList<T>` / `persistentListOf()` from `kotlinx.collections.immutable` (matches `mobile-apps/AGENTS.md` Compose Stability rules) |
| `compose:preview-naming-check` | `@Preview` function named with past tense (`OnClickedPreview`) | Rename to present tense, `Preview` suffix, e.g. `ChargerStatusCardChargingPreview` |
| `compose:preview-public-check` | `@Preview` function is public | Make it `private` (see `.claude/rules/compose-previews.md`) |
| `standard:trailing-comma-on-declaration-site` / `-on-call-site` | Missing trailing comma in multiline call/decl | `spotlessApply` adds it — no manual fix needed |

If a rule isn't in this table, treat the ktlint message as authoritative and search the `io.nlopez.compose.rules:ktlint` docs (for the version in `libs.versions.toml`) by rule id for anything Compose-specific (`compose:*`); everything else is stock ktlint.

## Workflow

1. `./gradlew spotlessApply` (whole project, or scope to touched modules for speed).
2. `./gradlew spotlessCheck` to confirm the diff is gone.
3. For anything still failing, read the `(rule-id)` from the error, match it in the table above, and hand-fix — these are semantic (Compose structure, stability) and can't be auto-formatted.
4. Repeat step 1 after manual fixes, since hand edits can reintroduce formatting issues.

## Verification

- [ ] `./gradlew spotlessApply` ran with no errors
- [ ] `./gradlew spotlessCheck` passes clean (no diff)
- [ ] No manual reformatting was done by hand where `spotlessApply` could have done it
- [ ] Any remaining `compose:*` violations were fixed per the table above, not suppressed
