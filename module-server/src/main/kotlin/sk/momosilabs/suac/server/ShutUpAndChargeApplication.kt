package sk.momosilabs.suac.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import sk.momosilabs.suac.server.common.ApplicationProperties

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties::class)
@EnableAsync
@EnableScheduling
open class ShutUpAndChargeApplication

fun main(args: Array<String>) {
    runApplication<ShutUpAndChargeApplication>(*args)
}
