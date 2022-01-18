package dev.qixils.yahoo.api.plugins

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.qixils.yahoo.api.Message
import dev.qixils.yahoo.api.MessageReference
import dev.qixils.yahoo.api.OffsetDateTimeAdapter
import dev.qixils.yahoo.api.Page
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.time.OffsetDateTime
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

val gson: Gson = GsonBuilder()
    .serializeNulls()
    .registerTypeAdapter(typeOf<OffsetDateTime>().javaType, OffsetDateTimeAdapter())
    .disableHtmlEscaping()
    .create()
val messages = HashMap<MessageReference, Message>()
val messageIndices = HashMap<String, List<Int>?>()

fun Application.configureRouting() {
    routing {
        get("/message/{group}/{id}") {
            val group: String = call.parameters["group"] as String
            val id: Int? = call.parameters["id"]?.toIntOrNull()
            if (!isValid(group)) {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "A group by the name of '$group' could not be found")
                )
            } else if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "The argument 'id' could not be parsed")
                )
            } else {
                val reference = MessageReference(group, id)
                val message: Message? = fetchMessage(reference)
                if (message == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "A message with the id $id in the group '$group' could not be found")
                    )
                } else {
                    call.respond(message)
                }
            }
        }

        get("/messages/{group}/page/{page}") {
            val group: String = call.parameters["group"] as String
            val page: Int? = call.parameters["page"]?.toIntOrNull()
            if (!isValid(group)) {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "A group by the name of '$group' could not be found")
                )
            } else if (page == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "The argument 'page' could not be parsed")
                )
            } else if (page < 1) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "The page '$page' is out of bounds (must be greater than zero)")
                )
            } else {
                val messages = getFromOffset(group, page)
                if (messages.isEmpty()) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to "The page '$page' is out of bounds")
                    )
                }
                call.respond(
                    Page(
                        messages,
                        if (page > 1) page - 1 else null,
                        if (pageExists(group, page + 1)) page + 1 else null
                    )
                )
            }
        }
    }
}

fun isValid(group: String): Boolean {
    if (messageIndices.containsKey(group)) return messageIndices[group] != null
    val clazz = Application::class.java
    val baseURL = clazz.getResource("/groups/$group/")
    if (baseURL == null) {
        messageIndices[group] = null
        return false
    }
    val directory = File(baseURL.path)
    val files = directory.list { _: File, filename: String -> filename.endsWith(".json") }
    if (files == null) {
        messageIndices[group] = null
        return false
    }
    val indices = ArrayList<Int>()
    for (file in files as Array<String>) {
        val index: Int? = file.substringBeforeLast(".json").toIntOrNull()
        if (index != null)
            indices.add(index)
    }
    if (indices.isEmpty()) {
        messageIndices[group] = null
        return false
    }
    indices.sort()
    messageIndices[group] = indices
    return true
}

fun fetchMessage(reference: MessageReference): Message? {
    val cache: Message? = messages[reference]
    if (cache != null)
        return cache

    val text: String = Application::class.java
        .getResource("/groups/${reference.group}/${reference.id}.json")
        ?.readText() ?: return null

    val value: Message = gson.fromJson(text, Message::class.java)
    messages[reference] = value
    return value
}

fun getMessageId(group: String, pageOffset: Int): Int? {
    assert(pageOffset >= 0)
    assert(isValid(group))
    val indices = messageIndices[group]!!
    val offset = (pageOffset - 1) * 50
    return if (offset < indices.size) indices[offset] else null
}

fun pageExists(group: String, offset: Int): Boolean {
    if (!isValid(group)) return false
    return getMessageId(group, offset) != null
}

fun getFromOffset(group: String, offset: Int): List<Message> {
    val messages = ArrayList<Message>()
    var nextId: Int? = getMessageId(group, offset)
    while (messages.size < 50 && nextId != null) {
        val message = fetchMessage(MessageReference(group, nextId)) ?: break
        messages.add(message)
        nextId = message.nextInTime
    }
    return messages
}
