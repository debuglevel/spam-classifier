package de.debuglevel.spamclassifier.token

import java.util.*

data class AddTokenResponse(
    val id: UUID,
    val name: String,
) {
    constructor(token: Token) : this(
        token.id!!,
        token.name,
    )
}