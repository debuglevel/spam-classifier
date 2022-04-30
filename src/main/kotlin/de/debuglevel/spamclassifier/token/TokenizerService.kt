package de.debuglevel.spamclassifier.token

import jakarta.inject.Singleton
import mu.KotlinLogging

@Singleton
class TokenizerService {
    private val logger = KotlinLogging.logger {}

    fun tokenize(text: String, removeDuplicates: Boolean = true): List<String> {
        logger.debug { "Tokenizing..." }

        val tokens = run {
            val tokens = text.replace(Regex("\\W"), " ")
                .lowercase()
                .split(" ")
                .filterNot { it.isNullOrBlank() }

            when {
                removeDuplicates -> tokens.distinct()
                else -> tokens
            }
        }

        logger.debug { "Tokenized" }
        return tokens
    }
}