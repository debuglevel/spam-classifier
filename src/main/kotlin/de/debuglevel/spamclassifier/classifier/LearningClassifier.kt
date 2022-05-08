package de.debuglevel.spamclassifier.classifier

import de.debuglevel.spamclassifier.token.Category

/**
 * A classifier which can learn (in contrast to one which might e.g. use a fixed model).
 */
interface LearningClassifier : Classifier {
    /**
     * Learns a text as [Category.Spam] or [Category.Ham].
     */
    fun learn(text: String, category: Category)

    /**
     * Learns tokens as [Category.Spam] or [Category.Ham].
     */
    fun learn(tokens: List<String>, category: Category)
}