package sk.momosilabs.suac.server.common.model

import java.util.UUID

data class ErrorDTO(
    val userMessage: String,
    val errorIdentifier: UUID,
)
