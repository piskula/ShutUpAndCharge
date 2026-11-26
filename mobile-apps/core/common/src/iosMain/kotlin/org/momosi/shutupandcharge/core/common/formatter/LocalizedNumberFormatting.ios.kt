package org.momosi.shutupandcharge.core.common.formatter

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.NSNumberFormatterDecimalStyle

internal actual fun createPlatformNumberFormatter(): PlatformNumberFormatter = IosPlatformNumberFormatter()

private class IosPlatformNumberFormatter : PlatformNumberFormatter {

    override fun formatCurrency(amount: Double, currencyCode: String): String {
        val formatter = NSNumberFormatter()
        formatter.numberStyle = NSNumberFormatterCurrencyStyle
        formatter.currencyCode = currencyCode
        return formatter.stringFromNumber(NSNumber(amount)) ?: amount.toString()
    }

    override fun formatDecimal(value: Double, fractionDigits: Int): String {
        val formatter = NSNumberFormatter()
        formatter.numberStyle = NSNumberFormatterDecimalStyle
        formatter.minimumFractionDigits = fractionDigits.toULong()
        formatter.maximumFractionDigits = fractionDigits.toULong()
        return formatter.stringFromNumber(NSNumber(value)) ?: value.toString()
    }
}
