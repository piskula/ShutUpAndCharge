package org.momosi.shutupandcharge.core.common.formatter

class NumberFormatter {

    private val platformNumberFormatter = createPlatformNumberFormatter()

    fun formatEur(amount: Double): String = platformNumberFormatter.formatCurrency(amount, CURRENCY_EUR)

    fun formatKwh(kwh: Double): String = "${platformNumberFormatter.formatDecimal(kwh, KWH_FRACTION_DIGITS)} kWh"

    fun formatDecimal(value: Double, fractionDigits: Int): String = platformNumberFormatter.formatDecimal(value, fractionDigits)

    private companion object {
        const val CURRENCY_EUR = "EUR"
        const val KWH_FRACTION_DIGITS = 2
    }
}

internal interface PlatformNumberFormatter {
    fun formatCurrency(amount: Double, currencyCode: String): String

    fun formatDecimal(value: Double, fractionDigits: Int): String
}

internal expect fun createPlatformNumberFormatter(): PlatformNumberFormatter
