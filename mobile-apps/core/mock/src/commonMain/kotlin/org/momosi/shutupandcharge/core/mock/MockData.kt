package org.momosi.shutupandcharge.core.mock

import org.momosi.shutupandcharge.core.account.model.Account
import org.momosi.shutupandcharge.core.transaction.model.PendingTransaction
import org.momosi.shutupandcharge.core.transaction.model.Transaction
import org.momosi.shutupandcharge.user.model.User
import org.momosi.shutupandcharge.user.model.UserRole
import kotlin.math.roundToLong
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

internal const val DEMO_ACCOUNT_ID = 1L
internal const val DEMO_STATION_ID = "momosi-1"
internal const val DEMO_STATS_LINK = "https://charge.momosi.org/"
internal const val PRICE_PER_KWH = 0.30

private const val SEED = 42
private const val TRANSACTION_COUNT = 48
private const val TOP_UP_EVERY = 6
private const val DEMO_ACCOUNT_SHARE = 0.4

internal val DemoUser = User(
    id = demoKeycloakId(DEMO_ACCOUNT_ID),
    name = "Demo User",
    email = "demo@momosi.org",
    username = "demo",
    roles = setOf(UserRole.User, UserRole.Admin),
)

internal data class MockTransaction(val accountId: Long, val transaction: Transaction)

internal object MockData {

    fun accounts(): List<Account> = listOf(
        account(DEMO_ACCOUNT_ID, "Demo", "User", 23.40, verified = true, chip = "04D3M0C4"),
        account(2, "Jana", "Nováková", 41.10, verified = true, chip = "04A1B2C3"),
        account(3, "Peter", "Kováč", -3.20, verified = true, chip = null),
        account(4, "Lucia", "Horváthová", 12.75, verified = true, chip = "04F7E6D5"),
        account(5, "Martin", "Szabó", 0.0, verified = false, chip = null),
        account(6, "Eva", "Tóthová", 58.00, verified = true, chip = null),
        account(7, "Tomáš", "Varga", 7.35, verified = true, chip = "04C9B8A7"),
        account(8, "Zuzana", "Balážová", 0.0, verified = false, chip = null),
    )

    fun transactions(accounts: List<Account>, now: Instant): List<MockTransaction> {
        val random = Random(SEED)
        val chargingAccounts = accounts.filter { it.verifiedForCharging && it.id != DEMO_ACCOUNT_ID }
        var time = now - 5.hours
        return List(TRANSACTION_COUNT) { index ->
            val account = if (random.nextDouble() < DEMO_ACCOUNT_SHARE) accounts.first { it.id == DEMO_ACCOUNT_ID } else chargingAccounts.random(random)
            time -= (random.nextInt(18, 96)).hours + random.nextInt(0, 60).minutes
            if (index % TOP_UP_EVERY == TOP_UP_EVERY - 1) {
                topUp(account, amount = listOf(20.0, 30.0, 50.0).random(random), time = time, guid = "demo-$index")
            } else {
                charging(
                    account = account,
                    kwh = (random.nextDouble(4.0, 38.0) * 100).roundToLong() / 100.0,
                    time = time,
                    guid = "demo-$index",
                    byChip = account.chipUid != null && random.nextBoolean(),
                    withLink = random.nextDouble() < 0.4,
                )
            }
        }
    }

    fun pending(accounts: List<Account>, now: Instant): List<PendingTransaction> = listOf(
        PendingTransaction(id = 9001, number = 311, identifier = "04A1B2C3", energyMeter = 1_284_530, startedAt = now - 2.hours, accountName = accounts.nameOf(2)),
        PendingTransaction(id = 9002, number = 312, identifier = "app", energyMeter = 1_291_870, startedAt = now - 1.days - 3.hours, accountName = accounts.nameOf(DEMO_ACCOUNT_ID)),
        PendingTransaction(id = 9003, number = 313, identifier = "04C9B8A7", energyMeter = 1_302_115, startedAt = now - 3.days, accountName = accounts.nameOf(7)),
    )

    fun charging(
        account: Account,
        kwh: Double,
        time: Instant,
        guid: String,
        byChip: Boolean = false,
        withLink: Boolean = false,
    ) = MockTransaction(
        accountId = account.id,
        transaction = Transaction(
            id = guid,
            time = time,
            kwh = kwh,
            price = -roundCents(kwh * PRICE_PER_KWH),
            stationId = DEMO_STATION_ID,
            accountName = account.fullName,
            triggeredByChip = byChip,
            link = DEMO_STATS_LINK.takeIf { withLink },
        ),
    )

    fun topUp(
        account: Account,
        amount: Double,
        time: Instant,
        guid: String,
    ) = MockTransaction(
        accountId = account.id,
        transaction = Transaction(
            id = guid,
            time = time,
            kwh = 0.0,
            price = amount,
            stationId = null,
            accountName = account.fullName,
            triggeredByChip = false,
            link = null,
        ),
    )

    private fun account(
        id: Long,
        firstName: String,
        lastName: String,
        balance: Double,
        verified: Boolean,
        chip: String?,
    ) = Account(
        id = id,
        keycloakId = demoKeycloakId(id),
        firstName = firstName,
        lastName = lastName,
        balance = balance,
        verifiedForCharging = verified,
        chipUid = chip,
    )

    private fun List<Account>.nameOf(id: Long): String = first { it.id == id }.fullName
}

internal fun demoKeycloakId(accountId: Long): String = "00000000-demo-0000-0000-" + accountId.toString().padStart(12, '0')

internal fun roundCents(value: Double): Double = (value * 100).roundToLong() / 100.0
