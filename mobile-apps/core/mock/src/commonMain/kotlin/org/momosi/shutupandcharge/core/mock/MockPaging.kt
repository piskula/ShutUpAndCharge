package org.momosi.shutupandcharge.core.mock

import org.momosi.shutupandcharge.core.common.model.Page
import kotlin.time.Duration.Companion.milliseconds

internal val MOCK_LATENCY = 350.milliseconds

internal fun <T> List<T>.toPage(page: Int, size: Int): Page<T> {
    val safeSize = size.coerceAtLeast(1)
    val content = drop(page * safeSize).take(safeSize)
    return Page(
        totalElements = this.size.toLong(),
        totalPages = (this.size + safeSize - 1) / safeSize,
        number = page,
        size = safeSize,
        numberOfElements = content.size,
        content = content,
    )
}
