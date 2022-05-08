package de.debuglevel.spamclassifier.token

import jakarta.inject.Singleton
import mu.KotlinLogging

@Singleton
class TokenizerService {
    private val logger = KotlinLogging.logger {}

    fun tokenize(text: String, removeDuplicates: Boolean = true): List<String> {
        logger.trace { "Tokenizing ${text.length}-characters text..." }

        val tokens = run {
            val tokens = text.replace(Regex("\\W"), " ")
                .lowercase()
                .split(" ")
                .filterNot { it.isBlank() }

            when {
                removeDuplicates -> tokens.distinct()
                else -> tokens
            }
        }

        logger.trace { "Tokenized ${text.length}-characters text into ${tokens.size} tokens." }
        return tokens
    }
}