package de.debuglevel.spamclassifier.token

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenizerServiceTests {

    @Inject
    lateinit var tokenizerService: TokenizerService

    @ParameterizedTest
    @MethodSource("textProvider")
    fun `tokenize text to distinct tokens`(textTestData: TestDataProvider.TextTestData) {
        // Arrange

        // Act
        val tokens = tokenizerService.tokenize(textTestData.text, true)

        // Assert
        assertThat(tokens).containsExactlyElementsOf(textTestData.tokens.distinct())
    }

    @ParameterizedTest
    @MethodSource("textProvider")
    fun `tokenize text retaining duplicates`(textTestData: TestDataProvider.TextTestData) {
        // Arrange

        // Act
        val tokens = tokenizerService.tokenize(textTestData.text, false)

        // Assert
        assertThat(tokens).containsExactlyElementsOf(textTestData.tokens)
    }

    fun textProvider() = TestDataProvider.textProvider()
}