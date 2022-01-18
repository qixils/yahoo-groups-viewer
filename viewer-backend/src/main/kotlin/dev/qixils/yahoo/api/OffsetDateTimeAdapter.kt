package dev.qixils.yahoo.api

import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.Type
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class OffsetDateTimeAdapter
    : TypeAdapter<OffsetDateTime>(),
    JsonSerializer<OffsetDateTime>,
    JsonDeserializer<OffsetDateTime> {

    override fun write(jsonWriter: JsonWriter, value: OffsetDateTime?) {
        if (value == null) {
            jsonWriter.nullValue()
        } else {
            jsonWriter.value(value.toEpochSecond())
        }
    }

    override fun read(value: JsonReader): OffsetDateTime? {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(value.nextLong()), ZoneOffset.UTC)
    }

    override fun serialize(
        value: OffsetDateTime?,
        type: Type,
        context: JsonSerializationContext
    ): JsonElement {
        if (value == null) return JsonNull.INSTANCE
        return JsonPrimitive(value.toEpochSecond())
    }

    override fun deserialize(
        value: JsonElement?,
        type: Type,
        context: JsonDeserializationContext
    ): OffsetDateTime? {
        if (value == null) return null;
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(value.asLong), ZoneOffset.UTC)
    }

}