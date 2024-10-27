package sk.momosilabs.suac.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class ShutUpAndChargeApplication

fun main(args: Array<String>) {
    runApplication<sk.momosilabs.suac.server.ShutUpAndChargeApplication>(*args)
}
