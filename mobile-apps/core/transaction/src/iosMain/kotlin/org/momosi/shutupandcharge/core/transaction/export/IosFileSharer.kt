package org.momosi.shutupandcharge.core.transaction.export

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.withContext
import org.momosi.shutupandcharge.core.common.coroutines.DispatcherProvider
import org.momosi.shutupandcharge.core.common.coroutines.runCatchingCancellable
import org.momosi.shutupandcharge.core.transaction.model.ExportedFile
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.popoverPresentationController

internal class IosFileSharer(private val dispatcherProvider: DispatcherProvider) : FileSharer {

    override suspend fun share(file: ExportedFile): Result<Unit> = runCatchingCancellable {
        val url = NSURL.fileURLWithPath(NSTemporaryDirectory() + file.fileName)
        check(file.bytes.toNSData().writeToURL(url, atomically = true)) { "Could not write ${file.fileName}" }
        withContext(dispatcherProvider.main()) { present(url) }
    }

    private fun present(url: NSURL) {
        val presenter = topViewController() ?: error("No view controller to present the share sheet from")
        val activityController = UIActivityViewController(activityItems = listOf(url), applicationActivities = null)
        activityController.popoverPresentationController?.sourceView = presenter.view
        presenter.presentViewController(activityController, animated = true, completion = null)
    }

    private fun topViewController(): UIViewController? {
        var controller = UIApplication.sharedApplication.keyWindow?.rootViewController
        while (controller?.presentedViewController != null) {
            controller = controller.presentedViewController
        }
        return controller
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData = usePinned { pinned ->
    NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
}
