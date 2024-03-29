package de.debuglevel.spamclassifier.text

import de.debuglevel.spamclassifier.classifier.ClassifierService
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import java.time.LocalDateTime

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/texts")
@Tag(name = "texts")
class TextController(
    private val textService: TextService,
    private val classifierService: ClassifierService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Learn a classified text.
     */
    @Post("/classified/")
    fun postOneClassifiedText(addClassifiedTextRequest: AddClassifiedTextRequest): HttpStatus {
        logger.debug("Called postOneClassifiedText()")

        return try {
            val seenOn = addClassifiedTextRequest.seenOn ?: LocalDateTime.now()
//            textService.learn(addClassifiedTextRequest.text, addClassifiedTextRequest.classification, seenOn)
            classifierService.learn(addClassifiedTextRequest.text, addClassifiedTextRequest.classification, seenOn)

            HttpStatus.CREATED
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpStatus.INTERNAL_SERVER_ERROR
        }
    }

    /**
     * Classify text.
     */
    @Post("/unclassified/")
    fun postOneUnclassifiedText(addUnclassifiedTextRequest: AddUnclassifiedTextRequest): HttpResponse<AddUnclassifiedTextResponse> {
        logger.debug("Called postOneUnclassifiedText()")

        return try {
            val seenOn = addUnclassifiedTextRequest.seenOn ?: LocalDateTime.now()

            val classifications = classifierService.classify(addUnclassifiedTextRequest.text)

            val scores = classifications.map {
                it.key to mapOf(it.value.category to it.value.probability)
            }.toMap()

            val addUnclassifiedTextResponse = AddUnclassifiedTextResponse(scores)

            HttpResponse.created(addUnclassifiedTextResponse)
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }
}