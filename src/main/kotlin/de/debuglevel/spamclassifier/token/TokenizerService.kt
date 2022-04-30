package de.debuglevel.spamclassifier.token

import jakarta.inject.Singleton
import mu.KotlinLogging

@Singleton
class TokenizerService {
    private val logger = KotlinLogging.logger {}

    fun tokenize(text: String): List<String> {
        logger.debug { "Tokenizing..." }

        val tokens = text.replace(Regex("\\W"), " ")
            .lowercase()
            .split(" ")
            .filterNot { it.isNullOrBlank() }

        logger.debug { "Tokenized" }
        return tokens
    }
}