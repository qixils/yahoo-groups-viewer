package dev.qixils.yahoo.api.plugins

import dev.qixils.yahoo.api.OffsetDateTimeAdapter
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import java.time.OffsetDateTime
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
            serializeNulls()
            registerTypeAdapter(typeOf<OffsetDateTime>().javaType, OffsetDateTimeAdapter())
            disableHtmlEscaping()
        }
    }
}
