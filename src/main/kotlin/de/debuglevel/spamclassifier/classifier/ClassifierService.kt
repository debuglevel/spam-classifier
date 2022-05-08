package de.debuglevel.spamclassifier.classifier

import de.debuglevel.spamclassifier.classifier.naivebayes.NaiveBayesClassifier
import de.debuglevel.spamclassifier.classifier.opennlp.OpenNLPClassifier
import de.debuglevel.spamclassifier.token.Category
import de.debuglevel.spamclassifier.token.TokenizerService
import io.micronaut.context.ApplicationContext
import jakarta.inject.Singleton
import mu.KotlinLogging
import java.time.LocalDateTime

@Singleton
class ClassifierService(
    private val tokenizerService: TokenizerService,
    private val naiveBayesClassifier: NaiveBayesClassifier,
    private val openNLPClassifier: OpenNLPClassifier,
    private val applicationContext: ApplicationContext,
) {
    private val logger = KotlinLogging.logger {}

    private val classifiers: Set<Classifier>
        get() {
            return applicationContext.getBeansOfType(Classifier::class.java).toSet()
        }

    /**
     * Resets all classifiers which support it.
     */
    fun resetAll() {
        logger.debug { "Resetting all classifiers..." }

        val resettableClassifiers = classifiers.filter { it.supportsReset }
        resettableClassifiers.forEach { it.reset() }

        logger.debug { "Reset $resettableClassifiers classifiers" }
    }

    fun startLearning() {
        logger.debug { "Starting learning on all classifiers..." }

        val batchLearningClassifiers = classifiers.filterIsInstance<BatchLearningClassifier>()
        batchLearningClassifiers.forEach { it.startLearning() }

        logger.debug { "Started learning on $batchLearningClassifiers classifiers" }
    }

    fun stopLearning() {
        logger.debug { "Stopping learning on all classifiers..." }

        val batchLearningClassifiers = classifiers.filterIsInstance<BatchLearningClassifier>()
        batchLearningClassifiers.forEach { it.stopLearning() }

        logger.debug { "Stopped learning on $batchLearningClassifiers classifiers" }
    }

    fun learn(text: String, category: Category, seenOn: LocalDateTime = LocalDateTime.now()) {
        logger.trace { "Learning ${text.length}-characters text as $category..." }

        val tokens = tokenizerService.tokenize(text)
        val learningClassifiers = classifiers.filterIsInstance<LearningClassifier>()
        learningClassifiers.forEach { it.learn(tokens, category) }

        logger.trace { "Learned ${text.length}-characters text (${tokens.count()} tokens) as $category" }
    }

    fun classify(text: String): Map<String, Classification> {
        logger.trace { "Classifying ${text.length}-characters text..." }

        val tokens = tokenizerService.tokenize(text)
        val classifications = classifiers.associateBy({ it.name }, { it.classify(tokens) })

        logger.trace { "Classified ${text.length}-characters text (${tokens.count()} tokens): $classifications" }
        return classifications
    }
}