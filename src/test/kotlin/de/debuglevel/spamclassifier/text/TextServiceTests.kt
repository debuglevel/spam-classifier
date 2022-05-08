package de.debuglevel.spamclassifier.text

import de.debuglevel.spamclassifier.token.Category
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TextServiceTests {

    @Inject
    lateinit var textService: TextService

    @Test
    fun `learning text increases tokens`() {
        // Arrange
        val text = "Hello, this is a test."

        // Act
        textService.learn(text, Category.Spam)

        // Assert
        // TODO: ensure 5 calls on tokenService.increase with equivalent spam class
    }
}