package de.debuglevel.spamclassifier.token

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenServiceTests {

    @Inject
    lateinit var tokenService: TokenService

    @ParameterizedTest
    @MethodSource("tokenProvider")
    fun `add token`(token: Token) {
        // Arrange

        // Act
        val addedToken = tokenService.add(token)

        // Assert
        assertThat(addedToken).isEqualTo(token)
    }

    @ParameterizedTest
    @MethodSource("tokenProvider")
    fun `get token`(token: Token) {
        // Arrange
        val addedToken = tokenService.add(token)

        // Act
        val gotToken = tokenService.get(addedToken.id!!)

        // Assert
        assertThat(gotToken).isEqualTo(addedToken)
    }

    @ParameterizedTest
    @MethodSource("tokenProvider")
    fun `token exists`(token: Token) {
        // Arrange
        val addedToken = tokenService.add(token)

        // Act
        val tokenExists = tokenService.exists(addedToken.id!!)

        // Assert
        assertThat(tokenExists).isTrue
    }

    @Test
    fun `count tokens`() {
        val tokens = tokenProvider().toList()

        val initialTokenCount = tokenService.count

        tokens.forEachIndexed { index, token ->
            // Arrange
            tokenService.add(token)

            // Act
            val tokenCount = tokenService.count

            // Assert
            assertThat(tokenCount).isEqualTo(initialTokenCount + index + 1)
        }
    }

    @Test
    fun `update token`() {
        // Arrange
        val token = Token(null, "Test")
        val addedToken = tokenService.add(token)

        // Act
        val gotToken = tokenService.get(addedToken.id!!)
        gotToken.name = "Test updated"
        val updatedToken = tokenService.update(gotToken.id!!, gotToken)

        // Assert
        assertThat(updatedToken.name).isEqualTo("Test updated")
    }

    /**
     * Test updating a copy of the original entity, because this way it's ensured that the service can handle detached entities.
     */
    @Test
    fun `update token with copy()`() {
        // Arrange
        val token = Token(null, "Test")
        val addedToken = tokenService.add(token)

        // Act
        val gotToken = tokenService.get(addedToken.id!!)
        val updateToken = gotToken.copy(name = "Test updated")
        val updatedToken = tokenService.update(updateToken.id!!, updateToken)

        // Assert
        assertThat(updatedToken.name).isEqualTo("Test updated")
    }

    @Test
    fun `delete token`() {
        // Arrange
        val token = Token(null, "Test")
        val addedToken = tokenService.add(token)
        val tokenCount = tokenService.count

        // Act
        tokenService.delete(addedToken.id!!)
        val tokenExists = tokenService.exists(addedToken.id!!)
        val tokenCountAfterDeletion = tokenService.count

        // Assert
        assertThat(tokenExists).isFalse
        assertThat(tokenCountAfterDeletion).isEqualTo(tokenCount - 1)
    }

    @Test
    fun `delete all tokens`() {
        val tokens = tokenProvider().toList()

        // Arrange
        for (token in tokens) {
            tokenService.add(token)
        }

        // Act
        tokenService.deleteAll()
        val tokenCountAfterDeletion = tokenService.count

        // Assert
        assertThat(tokenCountAfterDeletion).isEqualTo(0)
    }

    fun tokenProvider() = TestDataProvider.tokenProvider()
}