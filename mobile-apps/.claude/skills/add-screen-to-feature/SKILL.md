---
name: add-screen-to-feature
description: Use when adding a new screen, page, or view to an existing feature module. Triggered by phrases like "add a screen", "create a new screen", "add a page", "new composable screen", "add a view", "new screen in feature", "add a detail screen to transactions/admin/profile".
---

# Skill: Add a Screen to an Existing Feature

> Adds a new screen — ViewModel, Composable, State, Action, and optional Event — to an existing `feature/*/impl` module (paths relative to `mobile-apps/`). All files go in `src/commonMain/kotlin/`. Existing features (`welcome`, `dashboard`, `transactions`, `profile`, `admin`) each have one screen whose files sit directly in the `impl` root package; a second screen gets its own sub-package.

## Triggers

Load this skill when the task matches **any** of these:
- Adding a new screen to an existing feature
- Task says "add a screen", "create a page", "add a new view/composable screen"
- A new nav key is needed inside an existing feature's `impl` module
- A ViewModel needs to be created for a new screen

## Prerequisites

- The target `feature/*/impl` module already exists
- The `EntryProvider` file for the feature already exists in `impl/navigation/`
- The Koin module for the feature already exists in `impl/di/`

## Steps

### 1. Create the screen package

```
feature/<feature>/impl/src/commonMain/kotlin/org/momosi/shutupandcharge/feature/<feature>/impl/<screen>/
  <Screen>Screen.kt
  <Screen>ViewModel.kt
  <Screen>State.kt
  <Screen>Action.kt
  <Screen>Event.kt      ← omit if no one-shot events needed
```

### 2. Create `<Screen>State.kt`

Never put a `Throwable` in State — carry a `FailureReason` (`core:common/network`) instead.

```kotlin
package org.momosi.shutupandcharge.feature.<feature>.impl.<screen>

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import org.momosi.shutupandcharge.core.common.network.FailureReason

@Immutable
sealed interface <Screen>State {
    data object Loading : <Screen>State

    data class Failed(val reason: FailureReason) : <Screen>State

    data class Content(
        val items: ImmutableList<SomeDomainModel>,
    ) : <Screen>State
}
```

### 3. Create `<Screen>Action.kt`

```kotlin
package org.momosi.shutupandcharge.feature.<feature>.impl.<screen>

sealed interface <Screen>Action {
    data object Retry : <Screen>Action
    data class SelectItem(val id: Long) : <Screen>Action
}
```

### 4. Create `<Screen>Event.kt` (only if one-shot navigation or signals are needed)

```kotlin
package org.momosi.shutupandcharge.feature.<feature>.impl.<screen>

sealed interface <Screen>Event {
    data class NavigateToDetail(val id: Long) : <Screen>Event
}
```

### 5. Create `<Screen>ViewModel.kt`

Prefer one focused `MutableStateFlow` per independently-owned piece of state, `combine`d into the
exposed `State` (see `ProfileViewModel`; `core:common`'s `coroutines/Flows.kt` has typed `combine`
overloads above 5 flows).

```kotlin
package org.momosi.shutupandcharge.feature.<feature>.impl.<screen>

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.common.network.toFailureReason

class <Screen>ViewModel(
    private val someRepository: SomeRepository,
) : ViewModel() {

    private val content = MutableStateFlow<<Screen>State>(<Screen>State.Loading)

    val state: StateFlow<<Screen>State> = content
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), <Screen>State.Loading)

    private val _events = Channel<<Screen>Event>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        load()
    }

    fun onAction(action: <Screen>Action) {
        Logger.d("Action:<Screen>", action.toString())
        when (action) {
            <Screen>Action.Retry -> load()
            is <Screen>Action.SelectItem -> viewModelScope.launch {
                _events.send(<Screen>Event.NavigateToDetail(action.id))
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            content.value = <Screen>State.Loading
            someRepository.getData().fold(
                onSuccess = { content.value = <Screen>State.Content(it.toImmutableList()) },
                onFailure = { content.value = <Screen>State.Failed(it.toFailureReason()) },
            )
        }
    }
}
```

Repositories already log their failures — don't log the same failure again in the ViewModel.

### 6. Create `<Screen>Screen.kt`

User-facing strings come from the feature's compose resources (`Res.string.*`, English + Slovak —
see `.claude/rules/string-resources.md`). Give every interactive element a `testTag`
(see the `test-app-with-adb` skill for the naming convention).

```kotlin
package org.momosi.shutupandcharge.feature.<feature>.impl.<screen>

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.momosi.shutupandcharge.core.uilibrary.components.ErrorState
import org.momosi.shutupandcharge.core.uilibrary.components.LoadingSpinner
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme
import org.momosi.shutupandcharge.feature.<feature>.impl.resources.Res
import org.momosi.shutupandcharge.feature.<feature>.impl.resources.<screen>_load_failed

@Composable
internal fun <Screen>Screen(
    onNavigateToDetail: (Long) -> Unit,
    viewModel: <Screen>ViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is <Screen>Event.NavigateToDetail -> onNavigateToDetail(event.id)
            }
        }
    }

    <Screen>Content(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun <Screen>Content(
    state: <Screen>State,
    onAction: (<Screen>Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        <Screen>State.Loading -> LoadingSpinner(modifier = modifier)
        is <Screen>State.Failed -> ErrorState(
            reason = state.reason,
            message = stringResource(Res.string.<screen>_load_failed),
            onRetry = { onAction(<Screen>Action.Retry) },
            modifier = modifier,
        )
        is <Screen>State.Content -> { /* actual content */ }
    }
}

@Preview
@Composable
private fun <Screen>ContentPreview() {
    SuacTheme {
        <Screen>Content(
            state = <Screen>State.Content(items = persistentListOf(/* realistic sample data */)),
            onAction = {},
        )
    }
}
```

**Note:** `@Preview` functions live in the **same file** as the composable they preview, in
`commonMain` — `suac.compose` provides the multiplatform preview annotation there. Preview the
stateless `<Screen>Content`, never the ViewModel-backed `<Screen>Screen` (see
`.claude/rules/compose-previews.md`).

### 7. Add an internal nav key if the screen is navigated to with parameters

Internal nav keys (not the feature entry point) live in `impl/navigation/`, not `api/`:

```kotlin
// feature/<feature>/impl/src/commonMain/kotlin/.../feature/<feature>/impl/navigation/<Screen>NavKey.kt
package org.momosi.shutupandcharge.feature.<feature>.impl.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
internal data class <Screen>NavKey(val id: Long) : NavKey
```

Every `NavKey` that can end up in a back stack must also be registered in
`app/shared/src/commonMain/kotlin/org/momosi/shutupandcharge/app/AppNavKeySerializers.kt`
(`subclass(<Screen>NavKey::class, <Screen>NavKey.serializer())`). If the key is `internal`, expose its
serializer from the feature (e.g. a public `val <feature>NavKeySerializers` helper) rather than
making the key public.

### 8. Register the entry in the EntryProvider

In `feature/<feature>/impl/src/commonMain/kotlin/.../navigation/<Feature>EntryProvider.kt`, add (the
entry function takes `navigator: Navigator` from `core:navigation` if it doesn't yet — update its call
in `app/shared/.../SuacApp.kt` too):

```kotlin
entry<<Screen>NavKey> { key ->
    <Screen>Screen(
        id = key.id,
        onBack = navigator::goBack,
    )
}
```

### 9. Register the ViewModel in the Koin module

In `feature/<feature>/impl/src/commonMain/kotlin/.../di/<Feature>Module.kt`:

```kotlin
val <feature>Module = module {
    // ...existing declarations...
    viewModel { <Screen>ViewModel(get()) }
}
```

## Verification

- [ ] All files exist in the screen package (`Screen`, `ViewModel`, `State`, `Action`, and `Event` if needed) — in `src/commonMain/kotlin/`
- [ ] `<Screen>State` is `@Immutable`; list fields use `ImmutableList`; no `Throwable` and no lambdas in State
- [ ] `<Screen>Screen` does not import `androidx.compose.material3` — only `core:ui-library` components
- [ ] `<Screen>Screen` uses `koinViewModel()`, not `hiltViewModel()`
- [ ] New strings exist in both `values/strings.xml` and `values-sk/strings.xml`
- [ ] ViewModel is registered in the feature's Koin module
- [ ] Nav entry is registered in the EntryProvider, and the nav key in `AppNavKeySerializers.kt`
- [ ] `@Preview` functions are in the same file, in `commonMain`
- [ ] `cd mobile-apps && ./gradlew :app:android:assembleDebug` passes
- [ ] `cd mobile-apps && ./gradlew spotlessCheck` passes
