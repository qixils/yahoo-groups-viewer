package dev.qixils.yahoo.api.plugins

import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
            serializeNulls()
            disableHtmlEscaping()
        }
    }
}
