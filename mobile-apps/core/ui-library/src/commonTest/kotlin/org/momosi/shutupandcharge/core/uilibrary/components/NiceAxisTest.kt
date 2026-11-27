package org.momosi.shutupandcharge.core.uilibrary.components

import kotlin.test.Test
import kotlin.test.assertEquals

class NiceAxisTest {

    @Test
    fun roundsUpToReadableStepsThatCoverTheHighestBar() {
        assertEquals(ChartAxis(max = 100.0, ticks = listOf(0.0, 25.0, 50.0, 75.0, 100.0)), niceAxis(98.0))
        assertEquals(ChartAxis(max = 400.0, ticks = listOf(0.0, 100.0, 200.0, 300.0, 400.0)), niceAxis(312.4))
    }

    @Test
    fun keepsWholeKwhTicksForSmallValues() {
        assertEquals(ChartAxis(max = 1.0, ticks = listOf(0.0, 1.0)), niceAxis(0.6))
        assertEquals(ChartAxis(max = 6.0, ticks = listOf(0.0, 2.0, 4.0, 6.0)), niceAxis(4.2))
    }
}
