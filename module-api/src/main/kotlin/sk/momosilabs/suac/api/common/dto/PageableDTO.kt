package sk.momosilabs.suac.api.common.dto

data class PageableDTO(
    val page: Int = 0,
    val size: Int = 20,
    val sort: String? = null,
)
