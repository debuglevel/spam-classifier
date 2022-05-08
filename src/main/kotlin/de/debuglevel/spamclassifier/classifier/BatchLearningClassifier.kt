package de.debuglevel.spamclassifier.classifier

/**
 * A classifier which learns by providing it the full batch of training data.
 *
 * If the [LearningClassifier] is only a [BatchLearningClassifier],
 * the [learn] functions have no effect if called before [startLearning] and after [stopLearning].
 */
interface BatchLearningClassifier : LearningClassifier {
    /**
     * Starts learning.
     *
     * Texts can be fed via [learn].
     */
    fun startLearning()

    /**
     * Stops learning and enables the new model.
     */
    fun stopLearning()
}