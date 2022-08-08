package de.debuglevel.spamclassifier.classifier.opennlp

import de.debuglevel.spamclassifier.token.TokenizerService
import jakarta.inject.Singleton
import mu.KotlinLogging
import opennlp.tools.ml.maxent.GISTrainer
import opennlp.tools.util.TrainingParameters

@Singleton
class GisOpenNlpClassifier(
    private val tokenizerService: TokenizerService,
) : OpenNLPClassifier(tokenizerService) {
    private val logger = KotlinLogging.logger {}

    override fun getTrainingParameters(): TrainingParameters {
        val trainingParameters = TrainingParameters.defaultParams()
        trainingParameters.put(TrainingParameters.ALGORITHM_PARAM, GISTrainer.MAXENT_VALUE)
        trainingParameters.put(TrainingParameters.ITERATIONS_PARAM, 100)
        trainingParameters.put(TrainingParameters.CUTOFF_PARAM, 5)
        return trainingParameters
    }

    override val name = "OpenNLP-GIS"

}