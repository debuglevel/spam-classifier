package de.debuglevel.spamclassifier.text

import de.debuglevel.spamclassifier.token.SpamClass
import de.debuglevel.spamclassifier.token.TokenService
import de.debuglevel.spamclassifier.token.TokenizerService
import jakarta.inject.Singleton
import mu.KotlinLogging
import java.time.LocalDateTime

@Singleton
class TextService(
    private val tokenService: TokenService,
    private val tokenizerService: TokenizerService,
) {
    private val logger = KotlinLogging.logger {}

    fun learn(text: String, spamClass: SpamClass, seenOn: LocalDateTime = LocalDateTime.now()) {
        logger.debug { "Learning text as $spamClass..." }

        val tokens = tokenizerService.tokenize(text)
        tokens.forEach { tokenService.increase(it, spamClass, seenOn) }

        logger.debug { "Learned text (${tokens.count()} tokens) as $spamClass" }
    }
}