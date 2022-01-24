package dev.qixils.yahoo.api

import dev.qixils.yahoo.api.plugins.configureCORS
import dev.qixils.yahoo.api.plugins.configureHTTP
import dev.qixils.yahoo.api.plugins.configureRouting
import dev.qixils.yahoo.api.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 4001, host = "0.0.0.0") {
        configureRouting()
        configureHTTP()
        configureSerialization()
        configureCORS()
    }.start(wait = true)
}
