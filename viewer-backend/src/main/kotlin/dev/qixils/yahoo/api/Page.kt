package dev.qixils.yahoo.api

data class Page(
    val messages: List<Message>,
    val previousPageIndex: Int?,
    val nextPageIndex: Int?
)