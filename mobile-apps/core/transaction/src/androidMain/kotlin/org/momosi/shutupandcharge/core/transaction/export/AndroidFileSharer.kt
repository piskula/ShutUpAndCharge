package org.momosi.shutupandcharge.core.transaction.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import kotlinx.coroutines.withContext
import org.momosi.shutupandcharge.core.common.coroutines.DispatcherProvider
import org.momosi.shutupandcharge.core.common.coroutines.runCatchingCancellable
import org.momosi.shutupandcharge.core.transaction.model.ExportedFile
import java.io.File

private const val EXPORT_DIRECTORY = "exports"
private const val AUTHORITY_SUFFIX = ".exports"

internal class AndroidFileSharer(private val context: Context, private val dispatcherProvider: DispatcherProvider) : FileSharer {

    override suspend fun share(file: ExportedFile): Result<Unit> = runCatchingCancellable {
        val exportFile = withContext(dispatcherProvider.io()) {
            val directory = File(context.cacheDir, EXPORT_DIRECTORY).apply { mkdirs() }
            File(directory, file.fileName).apply { writeBytes(file.bytes) }
        }
        val uri = FileProvider.getUriForFile(context, context.packageName + AUTHORITY_SUFFIX, exportFile)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = file.mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(sendIntent, file.fileName).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        withContext(dispatcherProvider.main()) { context.startActivity(chooser) }
    }
}
