package dev.qixils.yahoo.api

class Message(
    var id: Int = 0,
    var authorId: Int = 0,
    var alias: String = "",
    var postDate: Int,
    var subject: String? = null,
    var body: String = "",
    var nextInTime: Int = 0
)