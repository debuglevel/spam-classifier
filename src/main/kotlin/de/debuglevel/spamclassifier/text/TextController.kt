package de.debuglevel.spamclassifier.text

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
class TextController(private val textService: TextService) {
    private val logger = KotlinLogging.logger {}

    /**
     * Learn a classified text.
     */
    @Post("/classified/")
    fun postOneClassifiedText(addTextRequest: AddTextRequest): HttpStatus {
        logger.debug("Called postOneSpamText()")

        return try {
            val seenOn = addTextRequest.seenOn ?: LocalDateTime.now()
            textService.learn(addTextRequest.text, addTextRequest.classification, seenOn)

            HttpStatus.CREATED
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
}