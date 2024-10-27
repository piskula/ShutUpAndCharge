package sk.momosilabs.trucker.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class ShutUpAndChargeApplication

fun main(args: Array<String>) {
    runApplication<ShutUpAndChargeApplication>(*args)
}
