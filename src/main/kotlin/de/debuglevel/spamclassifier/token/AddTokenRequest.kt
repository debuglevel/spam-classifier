package de.debuglevel.spamclassifier.token

data class AddTokenRequest(
    val name: String,
) {
    constructor(token: Token) : this(
        name = token.name
    )

    fun toToken(): Token {
        return Token(
            id = null,
            name = this.name,
        )
    }
}