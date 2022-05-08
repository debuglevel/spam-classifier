package de.debuglevel.spamclassifier.classifier.opennlp

import de.debuglevel.spamclassifier.classifier.BatchLearningClassifier
import de.debuglevel.spamclassifier.classifier.Classification
import de.debuglevel.spamclassifier.token.Category
import de.debuglevel.spamclassifier.token.TokenizerService
import jakarta.inject.Singleton
import mu.KotlinLogging
import opennlp.tools.doccat.*
import opennlp.tools.util.ObjectStream
import opennlp.tools.util.PlainTextByLineStream
import opennlp.tools.util.TrainingParameters
import java.io.*
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread

/**
 * Classifier based on Apache OpenNLP.
 */
@Singleton
class OpenNLPClassifier(
    private val tokenizerService: TokenizerService,
) : BatchLearningClassifier {
    private val logger = KotlinLogging.logger {}

    override val name = "OpenNLP-DocumentCategorizer"

    override val supportsReset = false

    /**
     * The OpenNLP main component to learn and classify texts.
     */
    private lateinit var documentCategorizer: DocumentCategorizerME

    /**
     * The [documentCategorizer] underlying [DoccatModel], which is not accessible through [DocumentCategorizerME].
     */
    private lateinit var model: DoccatModel

    /**
     * The [OutputStream] to write training data into for [DocumentCategorizerME.train].
     * Connected to [trainingDataInputStream].
     */
    private lateinit var trainingDataOutputStream: PipedOutputStream

    /**
     * [OutputStreamWriter] for the [trainingDataOutputStream].
     */
    private lateinit var trainingDataOutputStreamWriter: OutputStreamWriter

    /**
     * The [InputStream] needed to feed the training data into [DocumentCategorizerME.train].
     * Connected to [trainingDataOutputStream].
     */
    private lateinit var trainingDataInputStream: PipedInputStream

    /**
     * [Thread] in which the [DocumentCategorizerME.train]ing happens.
     * This must be a separate thread because the necessary [InputStream] is connected via an [PipedOutputStream].
     */
    private var learningThread: Thread? = null

    /**
     * (Re)initializes the training data streams.
     */
    private fun initializeTrainingDataStream() {
        logger.trace { "Initializing training data streams..." }

        trainingDataOutputStream = PipedOutputStream()
        trainingDataInputStream = PipedInputStream()
        trainingDataOutputStream.connect(trainingDataInputStream)

        trainingDataOutputStreamWriter = trainingDataOutputStream.writer()

        logger.trace { "Initialized training data streams" }
    }

    override fun reset() {
        throw UnsupportedOperationException("Cannot be reset; only learned from scratch again.")
    }

    override fun learnPersistedTokens() {
        TODO()

//        logger.trace { "Learning persisted tokens..." }
//
//        val tokens: Set<Token> = tokenService.getAll()
//        tokens.sortedBy { it.lastSeenOn }
//            .forEach { token ->
//                repeat(token.spamCount) { learn(Category.Spam, listOf(token.text)) }
//                repeat(token.hamCount) { learn(Category.Ham, listOf(token.text)) }
//            }
//
//        logger.trace { "Learned persisted tokens (${tokens.count()} tokens, ${bayes.categoriesTotal} categories, ${bayes.features.size} features)" }
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

        val tokensArray = tokens.toTypedArray()
        val categoryScores = this.documentCategorizer.scoreMap(tokensArray)
        val (categoryString, score) = categoryScores
            .maxByOrNull { it.value }!!
            .toPair()

        val category = Category.valueOf(categoryString)

        val classification = Classification(category, score)

        logger.trace { "Classified ${tokens.size} tokens as ${classification.category} with probability=${classification.probability}" }
        return classification
    }

    override fun load(inputStream: InputStream) {
        this.documentCategorizer = buildDocumentCategorizer(inputStream)
    }

    override fun save(outputStream: OutputStream) {
        logger.debug { "Saving model $model to OutputStream $outputStream..." }
        this.model.serialize(outputStream)
        logger.debug { "Saved model $model to OutputStream $outputStream" }
    }

    /**
     * Builds a [DocumentCategorizer] from a deserialized [modelInputStream].
     */
    private fun buildDocumentCategorizer(modelInputStream: InputStream): DocumentCategorizerME {
        logger.trace { "Building DocumentCategorizer from modelInputStrean $modelInputStream..." }

        val model = DoccatModel(modelInputStream)
        val documentCategorizer = buildDocumentCategorizer(model)

        logger.trace { "Built DocumentCategorizer from model $modelInputStream: $documentCategorizer" }
        return documentCategorizer
    }

    /**
     * Builds a [DocumentCategorizerME] from a [DoccatModel].
     */
    private fun buildDocumentCategorizer(model: DoccatModel): DocumentCategorizerME {
        logger.trace { "Building DocumentCategorizer from model $model..." }

        val documentCategorizer = DocumentCategorizerME(model)

        logger.trace { "Built DocumentCategorizer from model $model: $documentCategorizer" }
        return documentCategorizer
    }

    /**
     * Starts learning.
     *
     * The previous [documentCategorizer] and [model] remain enabled until the learning is stopped.
     */
    override fun startLearning() {
        logger.debug { "Starting learning..." }

        val learningThreadRunning = this.learningThread?.isAlive == true

        if (!learningThreadRunning) {
            initializeTrainingDataStream()

            logger.trace { "Starting learning thread..." }
            this.learningThread = thread(
                start = true,
                isDaemon = true,
                name = "OpenNLP-Train",
            ) {
                // Blocks until the stream is closed.
                val model = trainModel(trainingDataInputStream)
                val buildDocumentCategorizer = buildDocumentCategorizer(model)

                // Assign after building DocumentCategorizer in case of a failure
                this.documentCategorizer = buildDocumentCategorizer
                this.model = model
            }
            logger.trace { "Started learning thread" }
        }

        logger.debug { "Started learning" }
    }

    override fun stopLearning() {
        logger.debug { "Stopping learning..." }

        // Close stream which signals EOF on corresponding InputStream.
        this.trainingDataOutputStream.close()

        // Wait until the learning thread is finished.
        this.learningThread?.join()

        this.learningThread = null
    }

    /**
     * Trains a [DoccatModel] with a [trainingDataInputStream] format
     * as needed by OpenNLP and write it into [modelOutputStream].
     */
    private fun trainModel(trainingDataInputStream: InputStream): DoccatModel {
        logger.debug { "Training model..." }

        val trainingDataInputStreamFactory = AnyInputStreamFactory(trainingDataInputStream)

        // Streams line by line
        val lineStream: ObjectStream<String> =
            PlainTextByLineStream(trainingDataInputStreamFactory, StandardCharsets.UTF_8)
        // Parses training data lines
        // Hint: Could probably easily be implemented on our own, if it eases stuff.
        val sampleStream: ObjectStream<DocumentSample> = DocumentSampleStream(lineStream)

        // Trains the model
        val trainingParameters = TrainingParameters.defaultParams()
        val model = DocumentCategorizerME.train(
            "en",
            sampleStream,
            trainingParameters,
            DoccatFactory()
        )

        logger.debug { "Trained model: $model" }
        return model
    }

    override fun learn(text: String, category: Category) {
        logger.trace { "Learning ${text.length}-characters text as $category..." }

        val tokens = tokenizerService.tokenize(text, false)
        learn(tokens, category)

        logger.trace { "Learned ${text.length}-characters text as $category" }
    }

    override fun learn(tokens: List<String>, category: Category) {
        logger.trace { "Learning ${tokens.count()} tokens as $category..." }

        val line = buildTrainingLine(tokens, category)
        trainingDataOutputStreamWriter.write(line)
        trainingDataOutputStreamWriter.flush() // flushing the writer seems to be necessary

        logger.trace { "Learned ${tokens.count()} tokens as $category" }
    }

    /**
     * Transforms [tokens] and their [category] into an OpenNLP training line.
     */
    private fun buildTrainingLine(tokens: List<String>, category: Category): String {
        logger.trace { "Building training line for $category from ${tokens.size} tokens..." }

        // OpenNLP does not allow only a category on a training line
        if (tokens.isEmpty()) {
            return ""
        }

        val categoryString = when (category) {
            Category.Spam -> Category.Spam.name
            Category.Ham -> Category.Ham.name
        }

        val trainingLine = "$categoryString ${tokens.joinToString(" ")}\n"

        logger.trace { "Built training line for $category from ${tokens.size} tokens: $trainingLine" }
        return trainingLine
    }
}