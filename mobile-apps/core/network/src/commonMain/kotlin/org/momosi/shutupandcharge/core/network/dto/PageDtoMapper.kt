package org.momosi.shutupandcharge.core.network.dto

import org.momosi.shutupandcharge.core.common.model.Page
import org.momosi.shutupandcharge.dto.PageDto

fun <T, R> PageDto<T>.toPage(mapper: (T) -> R): Page<R> = Page(
    totalElements = totalElements,
    totalPages = totalPages,
    number = number,
    size = size,
    numberOfElements = numberOfElements,
    content = content.map(mapper),
)
