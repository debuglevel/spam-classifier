package de.debuglevel.spamclassifier.token

import de.debuglevel.spamclassifier.StringUtils
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenServiceTests {

    @Inject
    lateinit var tokenService: TokenService

    @Test
    fun `increased token can be get`() {
        // Arrange
        val token = StringUtils.createRandomString(10)

        // Act
        val increasedToken = tokenService.increase(token, Category.Spam, LocalDateTime.now())
        val gotToken = tokenService.get(token)

        // Assert
        assertThat(increasedToken.text).isEqualTo(token)
        assertThat(gotToken.text).isEqualTo(token)
        assertThat(increasedToken.spamCount).isGreaterThan(0)
        assertThat(gotToken.spamCount).isGreaterThan(0)
        assertThat(increasedToken.lastSeenOn).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS))
        assertThat(gotToken.lastSeenOn).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS))
    }

    @Test
    fun `increasing token increases only spam count`() {
        // Arrange
        val token = StringUtils.createRandomString(10)
        tokenService.increase(token, Category.Spam, LocalDateTime.now())
        val spamCountBefore = tokenService.get(token).spamCount
        val hamCountBefore = tokenService.get(token).hamCount

        // Act
        val increasedToken = tokenService.increase(token, Category.Spam, LocalDateTime.now())
        val gotToken = tokenService.get(token)

        // Assert
        assertThat(increasedToken.spamCount).isEqualTo(spamCountBefore + 1)
        assertThat(increasedToken.hamCount).isEqualTo(hamCountBefore)
        assertThat(gotToken.spamCount).isEqualTo(spamCountBefore + 1)
        assertThat(gotToken.hamCount).isEqualTo(hamCountBefore)
    }

    @Test
    fun `increasing token increases only ham count`() {
        // Arrange
        val token = StringUtils.createRandomString(10)
        tokenService.increase(token, Category.Ham, LocalDateTime.now())
        val spamCountBefore = tokenService.get(token).spamCount
        val hamCountBefore = tokenService.get(token).hamCount

        // Act
        val increasedToken = tokenService.increase(token, Category.Ham, LocalDateTime.now())
        val gotToken = tokenService.get(token)

        // Assert
        assertThat(increasedToken.hamCount).isEqualTo(hamCountBefore + 1)
        assertThat(increasedToken.spamCount).isEqualTo(spamCountBefore)
        assertThat(gotToken.hamCount).isEqualTo(hamCountBefore + 1)
        assertThat(gotToken.spamCount).isEqualTo(spamCountBefore)
    }

    @Test
    fun `increasing token with newer seenOn updates lastSeenOn`() {
        // Arrange
        val token = StringUtils.createRandomString(10)
        tokenService.increase(token, Category.Ham, LocalDateTime.now().minusHours(22))

        // Act
        val increasedToken = tokenService.increase(token, Category.Ham, LocalDateTime.now().minusHours(11))
        val gotToken = tokenService.get(token)

        // Assert
        assertThat(increasedToken.lastSeenOn).isCloseTo(
            LocalDateTime.now().minusHours(11),
            within(1, ChronoUnit.SECONDS)
        )
        assertThat(gotToken.lastSeenOn).isCloseTo(LocalDateTime.now().minusHours(11), within(1, ChronoUnit.SECONDS))
    }

    @Test
    fun `increasing token with older seenOn does not update lastSeenOn`() {
        // Arrange
        val token = StringUtils.createRandomString(10)
        tokenService.increase(token, Category.Ham, LocalDateTime.now().minusHours(11))

        // Act
        val increasedToken = tokenService.increase(token, Category.Ham, LocalDateTime.now().minusHours(22))
        val gotToken = tokenService.get(token)

        // Assert
        assertThat(increasedToken.lastSeenOn).isCloseTo(
            LocalDateTime.now().minusHours(11),
            within(1, ChronoUnit.SECONDS)
        )
        assertThat(gotToken.lastSeenOn).isCloseTo(LocalDateTime.now().minusHours(11), within(1, ChronoUnit.SECONDS))
    }

    @ParameterizedTest
    @MethodSource("tokenProvider")
    fun `increase token`(token: TestDataProvider.TokenTestData) {
        // Arrange
        var expectedSpamCount = 0
        var expectedHamCount = 0
        var expectedLastSeenOn = token.seenOn

        try {
            val gotToken = tokenService.get(token.text)
            expectedSpamCount = gotToken.spamCount
            expectedHamCount = gotToken.hamCount

            expectedLastSeenOn = setOf(token.seenOn, gotToken.lastSeenOn).maxOf { it }
        } catch (_: Exception) {
        }

        when (token.category) {
            Category.Spam -> expectedSpamCount++
            Category.Ham -> expectedHamCount++
        }

        // Act
        val increasedToken = tokenService.increase(token.text, token.category, token.seenOn)

        // Assert
        assertThat(increasedToken.text).isEqualTo(token.text)
        assertThat(increasedToken.spamCount).isEqualTo(expectedSpamCount)
        assertThat(increasedToken.hamCount).isEqualTo(expectedHamCount)
        assertThat(increasedToken.lastSeenOn).isEqualTo(expectedLastSeenOn)
    }

//    @ParameterizedTest
//    @MethodSource("tokenProvider")
//    fun `get token`(token: Token) {
//        // Arrange
//        val addedToken = tokenService.add(token)
//
//        // Act
//        val gotToken = tokenService.get(addedToken.id!!)
//
//        // Assert
//        assertThat(gotToken).isEqualTo(addedToken)
//    }

    @ParameterizedTest
    @MethodSource("tokenProvider")
    fun `increased token exists`(token: TestDataProvider.TokenTestData) {
        // Arrange

        // Act
        val increasedToken = tokenService.increase(token.text, token.category, token.seenOn)
        val tokenExists = tokenService.exists(increasedToken.text)

        // Assert
        assertThat(tokenExists).isTrue
    }

    @Test
    fun `count tokens`() {
        val tokens = tokenProvider().toList()

        val initialTokenCount = tokenService.count
        var expectedTokenCount = initialTokenCount

        tokens.forEachIndexed { index, token ->
            // Arrange
            if (!tokenService.exists(token.text)) {
                expectedTokenCount++
            }
            tokenService.increase(token.text, token.category, token.seenOn)

            // Act
            val tokenCount = tokenService.count

            // Assert
            assertThat(tokenCount).isEqualTo(expectedTokenCount)
        }
    }

//    @Test
//    fun `update token`() {
//        // Arrange
//        val token = Token(null, "Test")
//        val addedToken = tokenService.add(token)
//
//        // Act
//        val gotToken = tokenService.get(addedToken.id!!)
//        gotToken.text = "Test updated"
//        val updatedToken = tokenService.update(gotToken.id!!, gotToken)
//
//        // Assert
//        assertThat(updatedToken.text).isEqualTo("Test updated")
//    }
//
//    /**
//     * Test updating a copy of the original entity, because this way it's ensured that the service can handle detached entities.
//     */
//    @Test
//    fun `update token with copy()`() {
//        // Arrange
//        val token = Token(null, "Test")
//        val addedToken = tokenService.add(token)
//
//        // Act
//        val gotToken = tokenService.get(addedToken.id!!)
//        val updateToken = gotToken.copy(text = "Test updated")
//        val updatedToken = tokenService.update(updateToken.id!!, updateToken)
//
//        // Assert
//        assertThat(updatedToken.text).isEqualTo("Test updated")
//    }
//
//    @Test
//    fun `delete token`() {
//        // Arrange
//        val token = Token(null, "Test")
//        val addedToken = tokenService.add(token)
//        val tokenCount = tokenService.count
//
//        // Act
//        tokenService.delete(addedToken.id!!)
//        val tokenExists = tokenService.exists(addedToken.id!!)
//        val tokenCountAfterDeletion = tokenService.count
//
//        // Assert
//        assertThat(tokenExists).isFalse
//        assertThat(tokenCountAfterDeletion).isEqualTo(tokenCount - 1)
//    }

    @Test
    fun `delete all tokens`() {
        val tokens = tokenProvider().toList()

        // Arrange
        for (token in tokens) {
            tokenService.increase(token.text, token.category, token.seenOn)
        }

        // Act
        tokenService.deleteAll()
        val tokenCountAfterDeletion = tokenService.count

        // Assert
        assertThat(tokenCountAfterDeletion).isEqualTo(0)
    }

    fun tokenProvider() = TestDataProvider.tokenProvider()
}