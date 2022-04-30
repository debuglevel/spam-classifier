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
    fun `tokenize text`(tokenTestData: TestDataProvider.TokenTestData) {
        // Arrange

        // Act
        val tokens = tokenizerService.tokenize(tokenTestData.text)

        // Assert
        assertThat(tokens).containsExactlyElementsOf(tokenTestData.tokens)
    }

    fun textProvider() = TestDataProvider.textProvider()
}