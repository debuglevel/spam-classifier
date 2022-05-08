package de.debuglevel.spamclassifier.classifier

/**
 * A classifier which can enrich its model via [learn] while it's enabled (i.e. without rebuilding it).
 */
interface ProgressiveLearningClassifier : LearningClassifier