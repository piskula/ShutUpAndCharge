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
* Surface roles follow Material 3 component tokens: page `surface`, top bar / navigation bar / header
  filters `surfaceContainer`, cards (`Modifier.card`) `surfaceContainerHighest` (filled card), dialogs and
  pull-to-refresh `surfaceContainerHigh`, bottom sheet `surfaceContainerLow`. Never use
  `surfaceContainerLowest` for raised content — in dark theme it sits below the page.
* Domain components: `charger/ChargerStatusCard` (status pill, description, current selector,
  start/stop) and `transaction/TransactionRow` — this is why the module exposes `:core:charger` and
  `:core:transaction` as `api`.
* Every component has `@Preview`s in the same file. Strings in English and Slovak.