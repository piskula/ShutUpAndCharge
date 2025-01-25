package sk.momosilabs.suac.server.common

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application")
data class ApplicationProperties(
    val station: ApplicationPropertiesStation,
)

data class ApplicationPropertiesStation(
    val cloudStatusUrl: String,
    val cloudSetUrl: String,
    val cloudToken: String,
)
