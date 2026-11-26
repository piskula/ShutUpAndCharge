package org.momosi.shutupandcharge.core.transaction.dto

import org.momosi.shutupandcharge.core.transaction.model.TransactionFilter
import org.momosi.shutupandcharge.core.transaction.model.TransactionType
import org.momosi.shutupandcharge.dto.TransactionFinishedDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TransactionDtoMappersTest {

    @Test
    fun filterTypeMapsToOnlyCreditFlag() {
        assertNull(TransactionFilter(type = TransactionType.All).toDto().onlyCredit)
        assertEquals(true, TransactionFilter(type = TransactionType.TopUps).toDto().onlyCredit)
        assertEquals(false, TransactionFilter(type = TransactionType.Charging).toDto().onlyCredit)
    }

    @Test
    fun onlyMineFilterSendsKeycloakIdAsAccountId() {
        assertEquals(setOf("kc-1"), TransactionFilter(onlyAccountKeycloakId = "kc-1").toDto().accountIds)
        assertTrue(TransactionFilter().toDto().accountIds.isEmpty())
    }

    @Test
    fun positivePriceIsTopUpAndBlankLinkIsDropped() {
        val topUp = finished(price = 50.0, link = " ").toTransaction()
        val charging = finished(price = -4.2, link = "https://stats").toTransaction()

        assertTrue(topUp.isTopUp)
        assertNull(topUp.link)
        assertFalse(charging.isTopUp)
        assertEquals("https://stats", charging.link)
    }

    private fun finished(price: Double, link: String?) = TransactionFinishedDto(
        guid = "guid",
        time = "2026-11-26T10:30:00+01:00",
        kwh = 12.0,
        price = price,
        accountId = 1,
        accountName = "Martin",
        link = link,
    )
}
