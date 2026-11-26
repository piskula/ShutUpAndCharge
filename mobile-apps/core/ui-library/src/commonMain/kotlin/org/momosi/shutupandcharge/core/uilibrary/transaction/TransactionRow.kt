package org.momosi.shutupandcharge.core.uilibrary.transaction

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import org.momosi.shutupandcharge.core.transaction.display.TransactionDisplay
import org.momosi.shutupandcharge.core.uilibrary.components.TransactionListItem
import org.momosi.shutupandcharge.core.uilibrary.components.amountToneOf
import org.momosi.shutupandcharge.core.uilibrary.icons.SuacIcons
import org.momosi.shutupandcharge.core.uilibrary.resources.Res
import org.momosi.shutupandcharge.core.uilibrary.resources.charger_station_name
import org.momosi.shutupandcharge.core.uilibrary.resources.transaction_credit
import org.momosi.shutupandcharge.core.uilibrary.theme.SuacTheme

@Composable
fun TransactionRow(
    transaction: TransactionDisplay,
    showAccount: Boolean,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val place = if (transaction.isTopUp) {
        stringResource(Res.string.transaction_credit)
    } else {
        transaction.stationId ?: stringResource(Res.string.charger_station_name)
    }
    TransactionListItem(
        title = transaction.dateText,
        subtitle = if (showAccount) "$place · ${transaction.accountName}" else place,
        amount = transaction.priceText,
        amountTone = amountToneOf(transaction.price),
        detail = transaction.kwhText,
        leadingIcon = if (transaction.isTopUp) SuacIcons.TopUp else SuacIcons.Power,
        badgeIcon = SuacIcons.Rfid.takeIf { transaction.triggeredByChip },
        onOpenStats = transaction.link?.let { link -> { uriHandler.openUri(link) } },
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun TransactionRowPreview() {
    SuacTheme {
        TransactionRow(
            transaction = TransactionDisplay(
                id = "1",
                dateText = "Nov 21, 2026, 18:42",
                stationId = "Kutlíkova",
                accountName = "Martin Styk",
                priceText = "-4.12 €",
                price = -4.12,
                kwhText = "13.42 kWh",
                isTopUp = false,
                triggeredByChip = true,
                link = "https://example.com",
            ),
            showAccount = true,
        )
    }
}
