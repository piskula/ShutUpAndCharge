package sk.momosilabs.suac.server.transaction.stationConfig.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sk.momosilabs.suac.server.transaction.stationConfig.persistence.entity.StationConfigEntity

@Repository
interface StationConfigRepository: JpaRepository<StationConfigEntity, String>
