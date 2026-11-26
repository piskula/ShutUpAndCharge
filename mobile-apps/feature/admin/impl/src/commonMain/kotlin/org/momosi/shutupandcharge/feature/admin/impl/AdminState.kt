package org.momosi.shutupandcharge.feature.admin.impl

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import org.momosi.shutupandcharge.core.common.network.FailureReason

enum class AdminTab { Users, Pending }

@Immutable
data class AdminState(
    val tab: AdminTab = AdminTab.Users,
    val users: UsersContent = UsersContent.Loading,
    val pending: PendingContent = PendingContent.Loading,
    val isSyncing: Boolean = false,
    val isRefreshing: Boolean = false,
    val selectedUser: AccountItem? = null,
    val dialog: AdminDialog? = null,
)

@Immutable
data class AccountItem(val id: Long, val keycloakId: String, val name: String, val balanceText: String, val balance: Double, val verifiedForCharging: Boolean, val chipUid: String?)

@Immutable
data class PendingItem(val id: Long, val number: Int, val identifier: String, val accountName: String, val startedText: String, val energyMeter: Long)

@Immutable
sealed interface UsersContent {
    data object Loading : UsersContent
    data class Failed(val reason: FailureReason) : UsersContent
    data class Loaded(val items: ImmutableList<AccountItem>, val canLoadMore: Boolean, val isLoadingMore: Boolean) : UsersContent
}

@Immutable
sealed interface PendingContent {
    data object Loading : PendingContent
    data class Failed(val reason: FailureReason) : PendingContent
    data class Loaded(val items: ImmutableList<PendingItem>) : PendingContent
}

@Immutable
sealed interface AdminDialog {
    val account: AccountItem
    val isInProgress: Boolean

    data class TopUp(override val account: AccountItem, val amountText: String = "", override val isInProgress: Boolean = false) : AdminDialog {
        val amount: Double? get() = parseAmount(amountText)
    }

    data class ToggleVerified(override val account: AccountItem, override val isInProgress: Boolean = false) : AdminDialog

    data class EditChip(override val account: AccountItem, val chipUid: String, override val isInProgress: Boolean = false) : AdminDialog
}

internal fun parseAmount(text: String): Double? = text.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it > 0 }
