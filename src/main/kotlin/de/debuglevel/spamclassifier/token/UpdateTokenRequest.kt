package de.debuglevel.spamclassifier.token

data class UpdateTokenRequest(
    val name: String,
) {
    fun toToken(): Token {
        return Token(
            id = null,
            name = this.name,
        )
    }
}