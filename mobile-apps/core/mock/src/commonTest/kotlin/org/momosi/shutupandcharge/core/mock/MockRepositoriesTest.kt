package org.momosi.shutupandcharge.core.mock

import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.momosi.shutupandcharge.core.charger.model.ChargerState
import org.momosi.shutupandcharge.core.transaction.model.TransactionFilter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class MockRepositoriesTest {

    @Test
    fun chargingSessionChargesTheDemoAccount() = runTest {
        val backend = MockBackend()
        val charger = MockChargerRepository(backend, backgroundScope)
        val accounts = MockAccountRepository(backend)
        val transactions = MockTransactionRepository(backend)
        val balanceBefore = accounts.getMyBalance().getOrThrow()

        assertEquals(ChargerState.CarConnected, charger.refresh().getOrThrow().state)
        assertEquals(ChargerState.Charging, charger.startCharging().getOrThrow().state)
        advanceTimeBy(10.seconds)
        val stopped = charger.stopCharging().getOrThrow()

        assertEquals(ChargerState.Complete, stopped.state)
        assertTrue(stopped.chargedKwh > 0)
        val last = transactions.getLastChargings(page = 0, size = 1).getOrThrow().content.single()
        assertEquals(roundCents(stopped.chargedKwh), last.kwh)
        assertEquals(roundCents(balanceBefore + last.price), accounts.getMyBalance().getOrThrow())
        assertEquals(ChargerState.Free, charger.refresh().getOrThrow().state)
    }

    @Test
    fun startRequiresConnectedCar() = runTest {
        val charger = MockChargerRepository(MockBackend(), backgroundScope)

        assertTrue(charger.startCharging().isFailure)
    }

    @Test
    fun syncTurnsPendingIntoFinishedTransactions() = runTest {
        val transactions = MockTransactionRepository(MockBackend())
        val pendingCount = transactions.getPendingTransactions(page = 0, size = 100).getOrThrow().totalElements
        val finishedBefore = transactions.getTransactions(filter = defaultFilter, page = 0, size = 1).getOrThrow().totalElements

        transactions.syncPendingTransactions().getOrThrow()

        assertEquals(0, transactions.getPendingTransactions(page = 0, size = 100).getOrThrow().totalElements)
        assertEquals(finishedBefore + pendingCount, transactions.getTransactions(filter = defaultFilter, page = 0, size = 1).getOrThrow().totalElements)
    }

    private val defaultFilter = TransactionFilter()
}
