package dev.qixils.yahoo.api

data class SearchResults(
    val results: List<Message>,
    val isFinalPage: Boolean
)
