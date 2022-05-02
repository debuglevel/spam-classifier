package de.debuglevel.spamclassifier.text

import de.debuglevel.spamclassifier.token.SpamClass
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
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
        assertDoesNotThrow { textClient.add(addTextRequest).block() }
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
        assertDoesNotThrow { textClient.add(addTextRequest).block() }
    }
}