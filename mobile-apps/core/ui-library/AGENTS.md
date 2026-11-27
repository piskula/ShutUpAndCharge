# core:ui-library

All reusable Compose UI. Features never import material3 directly.

* Theme: `SuacTheme { }`, `AppTheme.colors` (SUAC Azure primary `#005CBB`, positive/warning/error roles),
  `AppTheme.typography`, `Shapes.CardShape`, `Modifier.card()`.
* `SuacIcons` (ImageVectors) and `SuacIllustrations` (EV charger illustrations, `*_illustration` drawables).
* Components: `Button`, `TextButton`, `IconButton`, `Chip`, `FilterChipRow`, `Switch`, `DashboardTopBar`,
  `Toolbar`, `NavigationBar`, `BottomSheet`, `ConfirmationDialog`, `InfoDialog`, `InputDialog`,
  `LoadingSpinner`, `SkeletonBox`, `ErrorState`, `ErrorSnackbarHost`, `PullToRefresh`, `SectionLabel`,
  `TransactionListItem`, `StackedBarChart` (value axis, selectable months, Total or by-series stacks; colours
  from `AppTheme.colors.chartSeries` / `chartOther`). `Modifier.card(position)` draws one row of a card that
  spans several lazy list items.
* Give icon-only buttons a `contentDescription` (`IconButton`, `TopBarIconButton`).
* Surface roles: page `surfaceContainer` (scheme `background`); top bar, header filters and navigation bar
  `surfaceDim`; cards (`Modifier.card`) `surfaceBright`. Material 3 keeps `surfaceDim` dimmest and
  `surfaceBright` brightest in both themes, so cards always sit lighter than the page and bars always frame
  it. Dialogs and pull-to-refresh `surfaceContainerHigh`, bottom sheet `surfaceContainerLow` (component
  tokens). Don't use `surfaceContainerLowest`/`Highest` for cards — their brightness flips between themes.
* Domain components: `charger/ChargerStatusCard` (status pill, description, current selector,
  start/stop) and `transaction/TransactionRow` — this is why the module exposes `:core:charger` and
  `:core:transaction` as `api`.
* Every component has `@Preview`s in the same file. Strings in English and Slovak.