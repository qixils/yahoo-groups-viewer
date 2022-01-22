package dev.qixils.yahoo.api

import java.util.*

class User (
    var id: Int = 0,
    var userName: String? = null,
    var knownAliases: Set<String> = Collections.emptySet(),
    var knownGroups: Set<String> = Collections.emptySet(),
    var fakeAccount: Boolean = false
)