package de.debuglevel.spamclassifier.classifier

import de.debuglevel.spamclassifier.token.Category
import java.io.InputStream
import java.io.OutputStream

interface Classifier {
    /**
     * Name to identify the classifier.
     */
    val name: String

    fun learnPersistedTokens()

    /**
     * Classifies a text into [Category.Spam] or [Category.Ham].
     */
    fun classify(text: String): Classification

    /**
     * Classifies tokens into [Category.Spam] or [Category.Ham].
     */
    fun classify(tokens: List<String>): Classification

    /**
     * Whether the classifier supports being reset to an unlearned state.
     */
    val supportsReset: Boolean

    /**
     * Resets the classifier to an unlearned state.
     *
     * The classifier usually does not work right after resetting
     * and must be initialized or learned again.
     *
     * Some classifiers might not support resetting.
     */
    fun reset()

    /**
     * Load the classifier (e.g. the underlying model) from [inputStream].
     */
    fun load(inputStream: InputStream)

    /**
     * Saves/Serializes the classifier (e.g. the underlying model) into [outputStream].
     */
    fun save(outputStream: OutputStream)
}