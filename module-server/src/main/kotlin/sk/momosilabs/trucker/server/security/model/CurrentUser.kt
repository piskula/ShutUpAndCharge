package sk.momosilabs.trucker.server.security.model

data class CurrentUser(
    val email: String,
    val identifier: String,
    val provider: String,
    val firstName: String,
    val lastName: String,
)
