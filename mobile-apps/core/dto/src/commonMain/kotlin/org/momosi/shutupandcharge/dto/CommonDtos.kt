package org.momosi.shutupandcharge.dto

import kotlinx.serialization.Serializable

@Serializable
data class PageDto<T>(val totalElements: Long = 0, val totalPages: Int = 0, val number: Int = 0, val size: Int = 0, val numberOfElements: Int = 0, val content: List<T> = emptyList())

@Serializable
data class ErrorDto(val userMessage: String? = null, val id: String? = null)

@Serializable
data class BuildInfoDto(val version: String, val name: String, val time: String? = null, val url: String = "")
