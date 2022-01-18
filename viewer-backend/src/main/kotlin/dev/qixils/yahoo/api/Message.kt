package dev.qixils.yahoo.api

import com.google.gson.annotations.SerializedName
import java.time.OffsetDateTime

class Message(
    var id: Int = 0,
    var userId: Int = 0,
    var userName: String = "",
    var realName: String = "",
    var displayName: String = "",
    @SerializedName("postDate")
    var postDateTime: OffsetDateTime = OffsetDateTime.MIN,
    var subject: String? = null,
    var body: String = "",
    var nextInTime: Int = 0
)