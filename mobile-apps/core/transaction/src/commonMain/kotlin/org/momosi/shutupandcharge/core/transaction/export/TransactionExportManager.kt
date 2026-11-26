package org.momosi.shutupandcharge.core.transaction.export

import org.momosi.shutupandcharge.core.common.logger.Logger
import org.momosi.shutupandcharge.core.transaction.TransactionRepository
import org.momosi.shutupandcharge.core.transaction.model.TransactionFilter

private const val TAG = "TransactionExportManager"

interface TransactionExportManager {
    suspend fun exportAndShare(filter: TransactionFilter): Result<Unit>
}

internal class TransactionExportManagerImpl(private val repository: TransactionRepository, private val fileSharer: FileSharer) : TransactionExportManager {

    override suspend fun exportAndShare(filter: TransactionFilter): Result<Unit> = repository.exportTransactions(filter).fold(
        onSuccess = { file -> fileSharer.share(file).onFailure { Logger.e(TAG, it, "Failed to share exported transactions") } },
        onFailure = { Result.failure(it) },
    )
}
