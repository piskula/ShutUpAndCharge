package sk.momosilabs.suac.server.transaction.stationConfig.persistence.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity(name = "station_config")
class StationConfigEntity(

    @Id
    val stationId: String,

    @field:NotNull
    var pricePerKwh: BigDecimal,

    var lastSuccessDownloadTimestampUtc: LocalDateTime?,

)
