package sk.momosilabs.suac.api.common.dto

import io.swagger.v3.oas.annotations.Parameter

data class PageableDTO(
    @field:Parameter(description = "Page number", required = true)
    val page: Int = 0,
    @field:Parameter(description = "Page size", required = true)
    val size: Int = 20,
    @field:Parameter(description = "Sort string", required = false)
    val sort: String? = null,
)
