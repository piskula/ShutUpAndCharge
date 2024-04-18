package sk.momosilabs.trucker.api.common.dto

data class PageDTO<T>(
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int,
    val numberOfElements: Int,
    val content: Collection<T>,
)
