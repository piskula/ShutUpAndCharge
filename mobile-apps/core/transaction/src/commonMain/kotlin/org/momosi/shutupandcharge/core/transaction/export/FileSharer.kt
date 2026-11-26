package org.momosi.shutupandcharge.core.transaction.export

import org.momosi.shutupandcharge.core.transaction.model.ExportedFile

interface FileSharer {
    suspend fun share(file: ExportedFile): Result<Unit>
}
