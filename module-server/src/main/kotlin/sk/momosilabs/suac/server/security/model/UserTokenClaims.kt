package sk.momosilabs.suac.server.security.model

data class UserTokenClaims(
    val email: String,
    val identifierSub: String,
    val provider: String,
    val firstName: String,
    val lastName: String,
    val roles: Set<String>,
)
