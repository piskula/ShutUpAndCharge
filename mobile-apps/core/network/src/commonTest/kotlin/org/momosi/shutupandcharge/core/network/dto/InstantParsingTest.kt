package org.momosi.shutupandcharge.core.network.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class InstantParsingTest {

    private val expected = Instant.parse("2026-11-26T09:30:00Z")

    @Test
    fun parsesOffsetDateTimeFromJackson() {
        assertEquals(expected, "2026-11-26T10:30:00+01:00".toInstantOrNull())
    }

    @Test
    fun parsesZonedDateTimeWithRegionId() {
        assertEquals(expected, "2026-11-26T10:30:00+01:00[Europe/Bratislava]".toInstantOrNull())
    }

    @Test
    fun treatsDateTimeWithoutZoneAsUtc() {
        assertEquals(expected, "2026-11-26T09:30:00".toInstantOrNull())
    }

    @Test
    fun returnsNullForGarbage() {
        assertNull("not a date".toInstantOrNull())
    }
}
