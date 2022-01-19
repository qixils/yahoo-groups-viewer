package dev.qixils.yahoo.api

import java.time.OffsetDateTime

class Message(
    var id: Int = 0,
    var user: User = User(),
    var postDate: OffsetDateTime = OffsetDateTime.MIN,
    var subject: String? = null,
    var body: String = "",
    var nextInTime: Int = 0
)