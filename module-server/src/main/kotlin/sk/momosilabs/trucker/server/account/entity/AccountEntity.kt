package sk.momosilabs.trucker.server.account.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.validation.constraints.NotNull

@Entity(name = "account")
class AccountEntity(

    @Id
    @field:NotNull
    val idKeycloak: String,

    @field:NotNull
    var firstName: String,

    @field:NotNull
    var lastName: String,
)
