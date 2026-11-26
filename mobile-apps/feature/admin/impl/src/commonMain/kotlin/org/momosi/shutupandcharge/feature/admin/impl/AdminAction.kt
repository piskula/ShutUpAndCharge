package org.momosi.shutupandcharge.feature.admin.impl

sealed interface AdminAction {
    data class SelectTab(val tab: AdminTab) : AdminAction
    data object Refresh : AdminAction
    data object RetryUsers : AdminAction
    data object LoadMoreUsers : AdminAction
    data object RetryPending : AdminAction
    data object SyncPending : AdminAction
    data class OpenUser(val accountId: Long) : AdminAction
    data object CloseUser : AdminAction
    data object StartTopUp : AdminAction
    data object StartToggleVerified : AdminAction
    data object StartEditChip : AdminAction
    data class UpdateDialogInput(val text: String) : AdminAction
    data object ConfirmDialog : AdminAction
    data object ClearChip : AdminAction
    data object DismissDialog : AdminAction
}
