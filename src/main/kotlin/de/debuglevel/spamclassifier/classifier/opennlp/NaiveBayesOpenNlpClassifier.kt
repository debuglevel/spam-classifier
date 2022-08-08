package de.debuglevel.spamclassifier.classifier.opennlp

import de.debuglevel.spamclassifier.token.TokenizerService
import jakarta.inject.Singleton
import mu.KotlinLogging
import opennlp.tools.ml.maxent.GISTrainer
import opennlp.tools.ml.maxent.GISTrainer.MAXENT_VALUE
import opennlp.tools.ml.naivebayes.NaiveBayesTrainer
import opennlp.tools.util.TrainingParameters

@Singleton
class NaiveBayesOpenNlpClassifier(
    private val tokenizerService: TokenizerService,
) : OpenNLPClassifier(tokenizerService) {
    private val logger = KotlinLogging.logger {}

    override fun getTrainingParameters(): TrainingParameters {
        val trainingParameters = TrainingParameters.defaultParams()
        trainingParameters.put(TrainingParameters.ALGORITHM_PARAM, NaiveBayesTrainer.NAIVE_BAYES_VALUE)
        return trainingParameters
    }

    override val name = "OpenNLP-NaiveBayes"

}