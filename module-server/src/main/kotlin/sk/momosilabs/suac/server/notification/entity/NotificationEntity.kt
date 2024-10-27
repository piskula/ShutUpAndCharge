package sk.momosilabs.suac.server.notification.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotNull
import sk.momosilabs.suac.server.account.entity.TransportCompanyEntity
import java.time.LocalDate

@Entity(name = "notification")
class NotificationEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne
    @field:NotNull
    val company: sk.momosilabs.suac.server.account.entity.TransportCompanyEntity,

    val validUntil: LocalDate?,

    )
