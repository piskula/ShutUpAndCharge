package org.momosi.shutupandcharge.core.common.formatter

import java.util.Currency
import java.util.Locale

internal actual fun createPlatformNumberFormatter(): PlatformNumberFormatter = AndroidPlatformNumberFormatter()

private class AndroidPlatformNumberFormatter : PlatformNumberFormatter {

    override fun formatCurrency(amount: Double, currencyCode: String): String = java.text.NumberFormat.getCurrencyInstance(Locale.getDefault())
        .apply { currency = Currency.getInstance(currencyCode) }
        .format(amount)

    override fun formatDecimal(value: Double, fractionDigits: Int): String = java.text.NumberFormat.getNumberInstance(Locale.getDefault())
        .apply {
            minimumFractionDigits = fractionDigits
            maximumFractionDigits = fractionDigits
        }
        .format(value)
}
