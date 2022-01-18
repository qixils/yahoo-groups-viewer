package dev.qixils.yahoo.api.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.*

fun Application.configureHTTP() {
    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }
    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }
}
