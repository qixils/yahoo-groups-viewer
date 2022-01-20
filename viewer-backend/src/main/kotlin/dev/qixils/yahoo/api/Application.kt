package dev.qixils.yahoo.api

import dev.qixils.yahoo.api.plugins.configureHTTP
import dev.qixils.yahoo.api.plugins.configureRouting
import dev.qixils.yahoo.api.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*

// TODO: ensure API is not exposed in search engine results in nginx

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureHTTP()
        configureSerialization()
    }.start(wait = true)
}
