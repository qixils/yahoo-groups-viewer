package dev.qixils.yahoo.api

import java.time.OffsetDateTime

class Message(
    var id: Int = 0,
    var authorId: Int = 0,
    var alias: String = "",
    var postDate: OffsetDateTime = OffsetDateTime.MIN,
    var subject: String? = null,
    var body: String = "",
    var nextInTime: Int = 0
)