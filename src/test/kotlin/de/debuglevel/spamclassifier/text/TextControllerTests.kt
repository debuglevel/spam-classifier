package de.debuglevel.spamclassifier.text

import de.debuglevel.spamclassifier.token.SpamClass
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDateTime

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TextControllerTests {
    @Inject
    lateinit var textClient: TextClient

    @Test
    fun `add spam text`() {
        // Arrange
        val addTextRequest = AddTextRequest(
            text = "Hello, my name is Harry.",
            classification = SpamClass.Ham,
            seenOn = LocalDateTime.now().minusHours(11)
        )

        // Act
        val httpResponse = textClient.addClassified(addTextRequest)

        // Assert
        assertThat(httpResponse.status()).isEqualTo(HttpStatus.CREATED)
        // TODO: ensure call on textService.learn and/or tokenService.increase
    }

    @Test
    fun `add ham text`() {
        // Arrange
        val addTextRequest = AddTextRequest(
            text = "Buy Bitcoin now.",
            classification = SpamClass.Spam,
            seenOn = LocalDateTime.now().minusHours(11)
        )

        // Act
        val httpResponse = textClient.addClassified(addTextRequest)

        // Assert
        assertThat(httpResponse.status()).isEqualTo(HttpStatus.CREATED)
        // TODO: ensure call on textService.learn and/or tokenService.increase
    }
}