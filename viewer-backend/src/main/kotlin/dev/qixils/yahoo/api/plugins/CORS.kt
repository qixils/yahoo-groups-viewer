package dev.qixils.yahoo.api.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*

fun Application.configureCORS() {
    install(CORS) {
        method(HttpMethod.Get)
        anyHost()
    }
}
