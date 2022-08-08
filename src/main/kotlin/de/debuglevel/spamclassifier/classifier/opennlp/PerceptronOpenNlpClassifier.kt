package de.debuglevel.spamclassifier.classifier.opennlp

import de.debuglevel.spamclassifier.token.TokenizerService
import jakarta.inject.Singleton
import mu.KotlinLogging
import opennlp.tools.ml.maxent.GISTrainer
import opennlp.tools.ml.perceptron.PerceptronTrainer
import opennlp.tools.util.TrainingParameters

@Singleton
class PerceptronOpenNlpClassifier(
    private val tokenizerService: TokenizerService,
) : OpenNLPClassifier(tokenizerService) {
    private val logger = KotlinLogging.logger {}

    override fun getTrainingParameters(): TrainingParameters {
        val trainingParameters = TrainingParameters.defaultParams()
        trainingParameters.put(TrainingParameters.ALGORITHM_PARAM, PerceptronTrainer.PERCEPTRON_VALUE)
        return trainingParameters
    }

    override val name = "OpenNLP-Perceptron"

}