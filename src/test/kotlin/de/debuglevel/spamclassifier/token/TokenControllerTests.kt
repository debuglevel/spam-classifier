package de.debuglevel.spamclassifier.token

import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.*

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenControllerTests {
    @Inject
    lateinit var tokenClient: TokenClient

    @ParameterizedTest
    @MethodSource("tokenProvider")
    fun `add token`(token: Token) {
        // Arrange
        val addTokenRequest = AddTokenRequest(token)

        // Act
        val addedToken = tokenClient.add(addTokenRequest).block()

        // Assert
        Assertions.assertThat(addedToken.name).isEqualTo(token.name)
        Assertions.assertThat(addedToken.name).isEqualTo(addTokenRequest.name)
    }

    @ParameterizedTest
    @MethodSource("tokenProvider")
    fun `get token`(token: Token) {
        // Arrange
        val addTokenRequest = AddTokenRequest(token)
        val addedToken = tokenClient.add(addTokenRequest).block()

        // Act
        val getToken = tokenClient.get(addedToken.id).block()

        // Assert
        Assertions.assertThat(getToken.id).isEqualTo(addedToken.id)
        Assertions.assertThat(getToken.name).isEqualTo(token.name)
        Assertions.assertThat(getToken.name).isEqualTo(addedToken.name)
    }

    @Test
    fun `get non-existing token`() {
        // Arrange

        // Act
        val getTokenResponse = tokenClient.get(UUID.randomUUID()).block()

        // Assert
        Assertions.assertThat(getTokenResponse).isNull()
    }

    @Test
    fun `update token`() {
        // Arrange
        val addTokenRequest = AddTokenRequest("Original Name")
        val addedToken = tokenClient.add(addTokenRequest).block()
        val updateTokenRequest = UpdateTokenRequest("Updated Name")

        // Act
        val updatedToken = tokenClient.update(addedToken.id, updateTokenRequest).block()
        val getToken = tokenClient.get(addedToken.id).block()

        // Assert
        Assertions.assertThat(updatedToken.id).isEqualTo(addedToken.id)
        Assertions.assertThat(getToken.id).isEqualTo(addedToken.id)
        Assertions.assertThat(updatedToken.name).isEqualTo(updateTokenRequest.name)
    }

    @Test
    fun `update non-existing token`() {
        // Arrange
        val updateTokenRequest = UpdateTokenRequest("Updated Name")

        // Act
        val getTokenResponse = tokenClient.update(UUID.randomUUID(), updateTokenRequest).block()

        // Assert
        Assertions.assertThat(getTokenResponse).isNull()
    }

    @Test
    fun `get all tokens`() {
        // Arrange
        tokenProvider().forEach {
            tokenClient.add(AddTokenRequest(it)).block()
        }

        // Act
        val getTokens = tokenClient.getAll()

        // Assert
        Assertions.assertThat(getTokens).extracting<String> { x -> x.name }
            .containsAll(tokenProvider().map { it.name }.toList())
    }

    @Test
    fun `get VIPs`() {
        // Arrange

        // Act
        val encodedCredentials =
            Base64.getEncoder().encodeToString("SECRET_USERNAME:SECRET_PASSWORD".byteInputStream().readBytes())
        val basicAuthenticationHeader = "Basic $encodedCredentials"
        val getTokens = tokenClient.getVIPs(basicAuthenticationHeader)

        // Assert
        Assertions.assertThat(getTokens).anyMatch { it.name == "Hermione Granger" }
        Assertions.assertThat(getTokens).anyMatch { it.name == "Harry Potter" }
        Assertions.assertThat(getTokens).anyMatch { it.name == "Ronald Weasley" }
    }

    @Test
    fun `fail get VIPs with bad authentication`() {
        // Arrange

        // Act
        val encodedCredentials =
            Base64.getEncoder().encodeToString("SECRET_USERNAME:wrongPassword".byteInputStream().readBytes())
        val basicAuthenticationHeader = "Basic $encodedCredentials"
        val thrown = Assertions.catchThrowable {
            tokenClient.getVIPs(basicAuthenticationHeader)
        }

        // Assert
        Assertions.assertThat(thrown)
            .isInstanceOf(HttpClientResponseException::class.java)
            .hasMessageContaining("Unauthorized")
    }

    fun tokenProvider() = TestDataProvider.tokenProvider()
}