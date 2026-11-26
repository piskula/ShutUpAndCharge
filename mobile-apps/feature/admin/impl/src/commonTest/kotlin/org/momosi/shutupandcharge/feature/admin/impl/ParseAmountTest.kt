package org.momosi.shutupandcharge.feature.admin.impl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ParseAmountTest {

    @Test
    fun acceptsDecimalCommaAndDot() {
        assertEquals(12.5, parseAmount("12,5"))
        assertEquals(12.5, parseAmount(" 12.5 "))
    }

    @Test
    fun rejectsZeroNegativeAndInvalidAmounts() {
        assertNull(parseAmount("0"))
        assertNull(parseAmount("-5"))
        assertNull(parseAmount("abc"))
        assertNull(parseAmount(""))
    }
}
