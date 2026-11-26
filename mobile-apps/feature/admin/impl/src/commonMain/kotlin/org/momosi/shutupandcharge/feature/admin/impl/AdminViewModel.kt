package org.momosi.shutupandcharge.feature.admin.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.momosi.shutupandcharge.core.account.AccountRepository
import org.momosi.shutupandcharge.core.account.model.Account
import org.momosi.shutupandcharge.core.common.coroutines.combine
import org.momosi.shutupandcharge.core.common.error.ErrorReporter
import org.momosi.shutupandcharge.core.common.formatter.DateFormatter
import org.momosi.shutupandcharge.core.common.formatter.NumberFormatter
import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.common.network.toFailureReason
import org.momosi.shutupandcharge.core.transaction.TransactionRepository
import org.momosi.shutupandcharge.core.transaction.model.PendingTransaction

private const val USERS_PAGE_SIZE = 50
private const val PENDING_PAGE_SIZE = 100

class AdminViewModel(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val numberFormatter: NumberFormatter,
    private val dateFormatter: DateFormatter,
    private val errorReporter: ErrorReporter,
) : ViewModel() {

    private val tab = MutableStateFlow(AdminTab.Users)
    private val users = MutableStateFlow<UsersContent>(UsersContent.Loading)
    private val pending = MutableStateFlow<PendingContent>(PendingContent.Loading)
    private val isSyncing = MutableStateFlow(false)
    private val isRefreshing = MutableStateFlow(false)
    private val selectedUserId = MutableStateFlow<Long?>(null)
    private val dialog = MutableStateFlow<AdminDialog?>(null)

    private var nextUsersPage = 0

    val state: StateFlow<AdminState> = combine(
        tab,
        users,
        pending,
        isSyncing,
        isRefreshing,
        selectedUserId,
        dialog,
    ) { currentTab, usersContent, pendingContent, syncing, refreshing, selectedId, currentDialog ->
        AdminState(
            tab = currentTab,
            users = usersContent,
            pending = pendingContent,
            isSyncing = syncing,
            isRefreshing = refreshing,
            selectedUser = (usersContent as? UsersContent.Loaded)?.items?.firstOrNull { it.id == selectedId },
            dialog = currentDialog,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AdminState(),
    )

    init {
        viewModelScope.launch { loadUsers() }
        viewModelScope.launch { loadPending() }
    }

    fun onAction(action: AdminAction) {
        Logger.d("Action:Admin", action.toString())
        when (action) {
            is AdminAction.SelectTab -> tab.value = action.tab
            AdminAction.Refresh -> refresh()
            AdminAction.RetryUsers -> viewModelScope.launch { loadUsers() }
            AdminAction.LoadMoreUsers -> loadMoreUsers()
            AdminAction.RetryPending -> viewModelScope.launch { loadPending() }
            AdminAction.SyncPending -> syncPending()
            is AdminAction.OpenUser -> selectedUserId.value = action.accountId
            AdminAction.CloseUser -> selectedUserId.value = null
            AdminAction.StartTopUp -> openDialog { AdminDialog.TopUp(it) }
            AdminAction.StartToggleVerified -> openDialog { AdminDialog.ToggleVerified(it) }
            AdminAction.StartEditChip -> openDialog { AdminDialog.EditChip(it, chipUid = it.chipUid.orEmpty()) }
            is AdminAction.UpdateDialogInput -> updateDialogInput(action.text)
            AdminAction.ConfirmDialog -> confirmDialog()
            AdminAction.ClearChip -> saveChip(chipUid = null)
            AdminAction.DismissDialog -> if (dialog.value?.isInProgress != true) dialog.value = null
        }
    }

    private fun refresh() {
        if (isRefreshing.value) return
        viewModelScope.launch {
            isRefreshing.value = true
            when (tab.value) {
                AdminTab.Users -> loadUsers()
                AdminTab.Pending -> loadPending()
            }
            isRefreshing.value = false
        }
    }

    private suspend fun loadUsers() {
        if (users.value !is UsersContent.Loaded) users.value = UsersContent.Loading
        accountRepository.getAccounts(page = 0, size = USERS_PAGE_SIZE)
            .onSuccess { page ->
                nextUsersPage = 1
                users.value = UsersContent.Loaded(
                    items = page.content.map { it.toItem() }.toImmutableList(),
                    canLoadMore = page.totalPages > 1,
                    isLoadingMore = false,
                )
            }
            .onFailure { error ->
                if (users.value is UsersContent.Loaded) errorReporter.report(error) else users.value = UsersContent.Failed(error.toFailureReason())
            }
    }

    private fun loadMoreUsers() {
        val current = users.value as? UsersContent.Loaded ?: return
        if (!current.canLoadMore || current.isLoadingMore) return
        users.value = current.copy(isLoadingMore = true)
        viewModelScope.launch {
            accountRepository.getAccounts(page = nextUsersPage, size = USERS_PAGE_SIZE)
                .onSuccess { page ->
                    nextUsersPage++
                    users.value = current.copy(
                        items = (current.items + page.content.map { it.toItem() }).toImmutableList(),
                        canLoadMore = nextUsersPage < page.totalPages,
                        isLoadingMore = false,
                    )
                }
                .onFailure {
                    errorReporter.report(it)
                    users.value = current.copy(isLoadingMore = false)
                }
        }
    }

    private suspend fun loadPending() {
        if (pending.value !is PendingContent.Loaded) pending.value = PendingContent.Loading
        pending.value = transactionRepository.getPendingTransactions(page = 0, size = PENDING_PAGE_SIZE).fold(
            onSuccess = { page -> PendingContent.Loaded(page.content.map { it.toItem() }.toImmutableList()) },
            onFailure = { error ->
                if (pending.value is PendingContent.Loaded) {
                    errorReporter.report(error)
                    pending.value
                } else {
                    PendingContent.Failed(error.toFailureReason())
                }
            },
        )
    }

    private fun syncPending() {
        if (isSyncing.value) return
        viewModelScope.launch {
            isSyncing.value = true
            transactionRepository.syncPendingTransactions()
                .onSuccess {
                    loadPending()
                    loadUsers()
                }
                .onFailure { errorReporter.report(it) }
            isSyncing.value = false
        }
    }

    private fun openDialog(create: (AccountItem) -> AdminDialog) {
        val selected = state.value.selectedUser ?: return
        selectedUserId.value = null
        dialog.value = create(selected)
    }

    private fun updateDialogInput(text: String) {
        dialog.value = when (val current = dialog.value) {
            is AdminDialog.TopUp -> current.copy(amountText = text)
            is AdminDialog.EditChip -> current.copy(chipUid = text)
            else -> current
        }
    }

    private fun confirmDialog() {
        when (val current = dialog.value) {
            is AdminDialog.TopUp -> topUp(current)
            is AdminDialog.ToggleVerified -> toggleVerified(current)
            is AdminDialog.EditChip -> saveChip(current.chipUid.trim().ifBlank { null })
            null -> Unit
        }
    }

    private fun topUp(current: AdminDialog.TopUp) {
        val amount = current.amount ?: return
        runDialogRequest(current, inProgress = current.copy(isInProgress = true)) {
            accountRepository.topUp(current.account.id, amount).map { loadUsers() }
        }
    }

    private fun toggleVerified(current: AdminDialog.ToggleVerified) {
        runDialogRequest(current, inProgress = current.copy(isInProgress = true)) {
            accountRepository.setVerifiedForCharging(current.account.id, !current.account.verifiedForCharging)
                .map { verified -> updateUser(current.account.id) { it.copy(verifiedForCharging = verified) } }
        }
    }

    private fun saveChip(chipUid: String?) {
        val current = dialog.value as? AdminDialog.EditChip ?: return
        runDialogRequest(current, inProgress = current.copy(isInProgress = true)) {
            accountRepository.setChipUid(current.account.id, chipUid)
                .map { updateUser(current.account.id) { it.copy(chipUid = chipUid) } }
        }
    }

    private fun runDialogRequest(
        original: AdminDialog,
        inProgress: AdminDialog,
        request: suspend () -> Result<Unit>,
    ) {
        if (original.isInProgress) return
        dialog.value = inProgress
        viewModelScope.launch {
            request()
                .onSuccess { dialog.value = null }
                .onFailure {
                    errorReporter.report(it)
                    dialog.value = original
                }
        }
    }

    private fun updateUser(accountId: Long, transform: (AccountItem) -> AccountItem) {
        val current = users.value as? UsersContent.Loaded ?: return
        users.value = current.copy(items = current.items.map { if (it.id == accountId) transform(it) else it }.toImmutableList())
    }

    private fun Account.toItem() = AccountItem(
        id = id,
        keycloakId = keycloakId,
        name = fullName,
        balanceText = numberFormatter.formatEur(balance),
        balance = balance,
        verifiedForCharging = verifiedForCharging,
        chipUid = chipUid,
    )

    private fun PendingTransaction.toItem() = PendingItem(
        id = id,
        number = number,
        identifier = identifier,
        accountName = accountName,
        startedText = startedAt?.let(dateFormatter::formatFullDateTime).orEmpty(),
        energyMeter = energyMeter,
    )
}
