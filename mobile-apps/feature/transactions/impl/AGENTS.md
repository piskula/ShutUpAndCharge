# feature:transactions:impl

Paged finished transactions (20 per page, infinite scroll, pull to refresh) shown as one grouped card, and
Excel export shared through the platform share sheet. Charging sessions with a stats link show a chart
action before the amount. Admins additionally get the filters (All/Top-ups/Charging, Only mine) and an
entry to the **Monthly usage** screen (`monthly/`, internal `MonthlyUsageNavKey(onlyMine)`): charged kWh per
month as an interactive bar chart (Total or By user), the selected month's per-user breakdown and the
period total. `transactionsNavKeySubclasses()` registers the internal key in `app:shared`.