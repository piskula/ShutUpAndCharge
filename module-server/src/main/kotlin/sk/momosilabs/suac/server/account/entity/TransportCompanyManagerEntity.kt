package sk.momosilabs.suac.server.account.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotNull

@Entity(name = "transport_company_manager")
class TransportCompanyManagerEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne
    @field:NotNull
    val company: sk.momosilabs.suac.server.account.entity.TransportCompanyEntity,

    @ManyToOne
    @field:NotNull
    val admin: sk.momosilabs.suac.server.account.entity.AccountEntity,

    )
