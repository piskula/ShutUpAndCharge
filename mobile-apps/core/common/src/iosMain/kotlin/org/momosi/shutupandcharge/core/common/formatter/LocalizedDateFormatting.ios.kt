package org.momosi.shutupandcharge.core.common.formatter

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.dateWithTimeIntervalSince1970
import kotlin.time.Instant

internal actual fun createPlatformDateFormatter(): PlatformDateFormatter = IosPlatformDateFormatter()

private class IosPlatformDateFormatter : PlatformDateFormatter {

    override fun formatDateTime(instant: Instant): String = format(instant, "MMM d, j:mm")

    override fun formatShortDate(instant: Instant): String = format(instant, "MMM d")

    override fun formatFullDateTime(instant: Instant): String = format(instant, "yMMMd j:mm")

    override fun formatMonthYear(instant: Instant): String = format(instant, "LLLL yyyy")

    override fun formatShortMonth(instant: Instant): String = format(instant, "LLL")

    private fun format(instant: Instant, template: String): String {
        val formatter = NSDateFormatter()
        formatter.setLocalizedDateFormatFromTemplate(template)
        return formatter.stringFromDate(instant.toNSDate())
    }
}

private fun Instant.toNSDate(): NSDate = NSDate.dateWithTimeIntervalSince1970(epochSeconds.toDouble())
