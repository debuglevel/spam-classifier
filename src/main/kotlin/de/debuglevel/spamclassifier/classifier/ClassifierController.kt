package de.debuglevel.spamclassifier.classifier

import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/classifiers")
@Tag(name = "classifiers")
class ClassifierController(
    private val classifierService: ClassifierService
) {
    private val logger = KotlinLogging.logger {}

    @Post("/reset")
    fun postReset(): HttpStatus {
        logger.debug("Called postReset()")

        return try {
            classifierService.resetAll()
            HttpStatus.CREATED
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpStatus.CREATED
        }
    }

    @Post("/start-learning")
    fun postStartLearning(): HttpStatus {
        logger.debug("Called postStartLearning()")

        return try {
            classifierService.startLearning()
            HttpStatus.CREATED
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpStatus.CREATED
        }
    }

    @Post("/stop-learning")
    fun postStopLearning(): HttpStatus {
        logger.debug("Called postStopLearning()")

        return try {
            classifierService.stopLearning()
            HttpStatus.CREATED
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpStatus.CREATED
        }
    }
}