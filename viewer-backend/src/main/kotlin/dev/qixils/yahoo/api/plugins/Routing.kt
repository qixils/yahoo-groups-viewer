package dev.qixils.yahoo.api.plugins

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.qixils.yahoo.api.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.time.OffsetDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.ceil
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

val gson: Gson = GsonBuilder()
    .serializeNulls()
    .disableHtmlEscaping()
    .create()
val messages = HashMap<MessageReference, Message?>()
val users = HashMap<Int, User?>()
val messageIndices = HashMap<String, List<Int>?>()
val allGroups = HashSet<String>()
const val MESSAGES_PER_PAGE = 50

// TODO: setup 404 page like {"error":"API endpoint not found"}
// TODO: init CORS

fun Application.configureRouting() {
    routing {
        get("/v1/groups") {
            call.respond(mapOf("groups" to getGroups()))
        }

        get("/v1/user/{id}") {
            val id: Int? = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "The argument 'id' could not be parsed")
                )
            } else if (!userExists(id)) {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "A user with the ID of '$id' could not be found")
                )
            } else {
                val user: User = getUser(id)!!
                call.respond(user)
            }
        }

        get("/v1/message/{group}/{id}") {
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

        get("/v1/messages/{group}/pages") {
            val group: String = call.parameters["group"] as String
            val pages: Int = if (isValid(group)) ceil(messageIndices[group]!!.size / 50f).toInt() else 0
            call.respond(mapOf("pages" to pages))
        }

        get("/v1/messages/{group}/page/{page}") {
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

        get("/v1/messages/{group}/search") {
            val group: String = call.parameters["group"] as String
            if (!isValid(group)) {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "A group by the name of '$group' could not be found")
                )
            } else {
                try {
                    call.respond(search(call, group))
                } catch (exc: java.lang.IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to exc.message))
                }
            }
        }
    }
}

fun userExists(id: Int): Boolean {
    return getUser(id) != null
}

fun getUser(id: Int): User? {
    if (users.containsKey(id)) return users[id]
    val file = File("data/users/${id}.json")
    if (!file.exists()) {
        users[id] = null
        return null
    }
    val user: User = gson.fromJson(File("data/users/${id}.json").readText(), User::class.java)
    users[id] = user
    return user
}

fun getGroups(): Set<String> {
    if (allGroups.isNotEmpty()) return allGroups
    val directory = File("data/groups/")
    for (file in directory.list()!!)
        allGroups.add(file)
    return allGroups
}

fun isValid(group: String): Boolean {
    if (messageIndices.containsKey(group)) return messageIndices[group] != null
    val directory = File("data/groups/$group/")
    if (!directory.exists()) {
        messageIndices[group] = null
        return false
    }
    val files = directory.list { _: File, filename: String -> filename.endsWith(".json") }
    if (files == null) {
        messageIndices[group] = null
        return false
    }
    val indices = ArrayList<Int>()
    for (file in files) {
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
    if (messages.containsKey(reference)) return messages[reference]

    val file = File("data/groups/${reference.group}/${reference.id}.json")
    if (!file.exists()) {
        messages[reference] = null
        return null
    }
    val text: String = file.readText()
    val value: Message = gson.fromJson(text, Message::class.java)
    messages[reference] = value
    return value
}

fun getMessageId(group: String, pageOffset: Int): Int? {
    assert(pageOffset >= 0)
    assert(isValid(group))
    val indices = messageIndices[group]!!
    val offset = (pageOffset - 1) * MESSAGES_PER_PAGE
    return if (offset < indices.size) indices[offset] else null
}

fun pageExists(group: String, offset: Int): Boolean {
    if (!isValid(group)) return false
    return getMessageId(group, offset) != null
}

fun getFromOffset(group: String, offset: Int): List<Message> {
    val messages = ArrayList<Message>()
    var nextId: Int? = getMessageId(group, offset)
    while (messages.size < MESSAGES_PER_PAGE && nextId != null) {
        val message = fetchMessage(MessageReference(group, nextId)) ?: break
        messages.add(message)
        nextId = message.nextInTime
    }
    return messages
}

fun search(call: ApplicationCall, group: String): SearchResults {
    if (!isValid(group))
        throw IllegalArgumentException("Provided group is not valid")
    val params = call.request.queryParameters
    if ("authorId" in params && params["authorId"]?.toIntOrNull() == null)
        throw IllegalArgumentException("'authorId' parameter '${params["authorId"]}' could not be parsed as an integer")
    if ("offset" in params && params["offset"]?.toIntOrNull() == null)
        throw IllegalArgumentException("'offset' parameter '${params["offset"]}' could not be parsed as an integer")
    if ("after" in params && params["after"]?.toIntOrNull() == null)
        throw IllegalArgumentException("'after' parameter '${params["after"]}' could not be parsed as an integer")
    if ("before" in params && params["before"]?.toIntOrNull() == null)
        throw IllegalArgumentException("'before' parameter '${params["before"]}' could not be parsed as an integer")

    val offset: Int = params["offset"]?.toInt() ?: 0
    val authorId: Int? = params["authorId"]?.toInt()
    val after: Int? = params["after"]?.toInt()
    val before: Int? = params["before"]?.toInt()

    val messages = ArrayList<Message>()
    var skipped = 0
    var metaIndex = 0
    val indices = messageIndices[group]!!
    var moreResultsFound = false
    while (metaIndex < indices.size) {
        val message: Message = fetchMessage(MessageReference(group, indices[metaIndex])) ?: continue
        metaIndex += 1

        // handle search queries
        if (authorId != null && authorId != message.authorId)
            continue
        if ("displayName" in params && !params["displayName"].equals(message.alias, ignoreCase = true))
            continue
        if ("userName" in params && !params["userName"].equals(getUser(message.authorId)?.userName, ignoreCase = true))
            continue
        if (after != null && message.postDate < after)
            continue
        if (before != null && message.postDate > before)
            continue

        // final handling
        if (skipped < offset) {
            skipped += 1
            continue
        } else if (messages.size == MESSAGES_PER_PAGE) {
            moreResultsFound = true
            break
        } else {
            messages.add(message)
        }
    }
    return SearchResults(messages, messages.size < MESSAGES_PER_PAGE || !moreResultsFound)
}
