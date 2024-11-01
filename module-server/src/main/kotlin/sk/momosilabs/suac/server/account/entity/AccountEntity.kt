package sk.momosilabs.suac.server.account.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotNull

@Entity(name = "account")
class AccountEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(unique = true)
    @field:NotNull
    val idKeycloak: String,

    @field:NotNull
    var firstName: String,

    @field:NotNull
    var lastName: String,

)
