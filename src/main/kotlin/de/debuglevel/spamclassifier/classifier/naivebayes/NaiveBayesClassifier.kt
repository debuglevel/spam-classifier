package de.debuglevel.spamclassifier.classifier.naivebayes

import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier
import de.debuglevel.spamclassifier.classifier.Classification
import de.debuglevel.spamclassifier.classifier.ProgressiveLearningClassifier
import de.debuglevel.spamclassifier.token.Category
import de.debuglevel.spamclassifier.token.Token
import de.debuglevel.spamclassifier.token.TokenService
import de.debuglevel.spamclassifier.token.TokenizerService
import jakarta.inject.Singleton
import mu.KotlinLogging
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream


@Singleton
class NaiveBayesClassifier(
    private val tokenService: TokenService,
    private val tokenizerService: TokenizerService,
) : ProgressiveLearningClassifier {
    private val logger = KotlinLogging.logger {}

    override val name = "naive-bayes"

    /**
     * The main component to learn and classify texts.
     */
    private var bayes: de.daslaboratorium.machinelearning.classifier.Classifier<String, Category> = BayesClassifier()

    override val supportsReset = true

    init {
        bayes.memoryCapacity = 100_000_000 // NOTE must be way greater than 1000 if tokens are learned on their own
    }

    private fun logStats() {
        logger.trace {
            "${bayes.categoriesTotal} categories (total) \t " +
                    "${bayes.features.size} features \t " +
                    "${bayes.categories.size} categories \t" +
                    "${bayes.memoryCapacity} memory capacity"
        }
    }

    override fun reset() {
        bayes.reset()
    }

    override fun load(inputStream: InputStream) {
        this.bayes = deserialize(inputStream)
    }

    /**
     * Deserializes an [inputStream] into a [BayesClassifier].
     */
    private fun deserialize(inputStream: InputStream): BayesClassifier<String, Category> {
        return ObjectInputStream(inputStream).use {
            it.readObject() as BayesClassifier<String, Category>
        }
    }

    override fun save(outputStream: OutputStream) {
        ObjectOutputStream(outputStream).use {
            it.writeObject(bayes)
        }
    }

    override fun learnPersistedTokens() {
        logger.trace { "Learning persisted tokens..." }

        val tokens: Set<Token> = tokenService.getAll()
        tokens.sortedBy { it.lastSeenOn }
            .forEach { token ->
                repeat(token.spamCount) { learn(listOf(token.text), Category.Spam) }
                repeat(token.hamCount) { learn(listOf(token.text), Category.Ham) }
            }

        logger.trace { "Learned persisted tokens (${tokens.count()} tokens, ${bayes.categoriesTotal} categories, ${bayes.features.size} features)" }
    }

    override fun learn(text: String, category: Category) {
        logger.trace { "Learning ${text.length}-characters text as $category..." }

        val tokens = tokenizerService.tokenize(text)
        learn(tokens, category)

        logger.trace { "Learned ${text.length}-characters text as $category" }
    }

    override fun learn(tokens: List<String>, category: Category) {
        logger.trace { "Learning ${tokens.count()} tokens as $category..." }

        bayes.learn(category, tokens)
        logStats()

        logger.trace { "Learned ${tokens.count()} tokens as $category" }
    }

    override fun classify(text: String): Classification {
        logger.trace { "Classifying ${text.length}-characters text..." }

        val tokens = tokenizerService.tokenize(text)
        val classification = classify(tokens)

        logger.trace { "Classified ${text.length}-characters text as ${classification.category} with probability=${classification.probability}" }
        return classification
    }

    override fun classify(tokens: List<String>): Classification {
        logger.trace { "Classifying ${tokens.size} tokens..." }

        val bayesClassification = bayes.classify(tokens)
        val classification = Classification(bayesClassification.category, bayesClassification.probability.toDouble())

        logger.trace { "Classified ${tokens.size} tokens as ${classification.category} with probability=${classification.probability}" }
        return classification
    }
}