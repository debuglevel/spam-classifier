package de.debuglevel.spamclassifier.text

import de.debuglevel.spamclassifier.classifier.naivebayes.NaiveBayesClassifier
import de.debuglevel.spamclassifier.classifier.opennlp.OpenNLPClassifier
import de.debuglevel.spamclassifier.token.Category
import de.debuglevel.spamclassifier.token.TokenService
import de.debuglevel.spamclassifier.token.TokenizerService
import jakarta.inject.Singleton
import mu.KotlinLogging
import java.time.LocalDateTime

@Singleton
class TextService(
    private val tokenService: TokenService,
    private val tokenizerService: TokenizerService,
    private val naiveBayesClassifier: NaiveBayesClassifier,
    private val openNLPClassifier: OpenNLPClassifier,
) {
    private val logger = KotlinLogging.logger {}

    fun learn(text: String, category: Category, seenOn: LocalDateTime = LocalDateTime.now()) {
        logger.trace { "Learning ${text.length}-characters text as $category..." }

        val tokens = tokenizerService.tokenize(text)
        tokens.forEach { tokenService.increase(it, category, seenOn) }

        //naiveBayesClassifier.learn(text, category)
//        openNLPClassifier.learn(text, category)

        logger.trace { "Learned ${text.length}-characters text (${tokens.count()} tokens) as $category" }
    }
}