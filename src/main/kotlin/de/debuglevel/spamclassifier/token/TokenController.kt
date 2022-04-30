package de.debuglevel.spamclassifier.token

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.server.types.files.StreamedFile
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import java.net.URI
import java.util.*

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/tokens")
@Tag(name = "tokens")
class TokenController(private val tokenService: TokenService) {
    private val logger = KotlinLogging.logger {}

    /**
     * Get all tokens
     * @return All tokens
     */
    @Get("/")
    fun getAllTokens(): HttpResponse<List<GetTokenResponse>> {
        logger.debug("Called getAllTokens()")
        return try {
            val tokens = tokenService.getAll()
            val getTokenResponses = tokens
                .map { GetTokenResponse(it) }

            HttpResponse.ok(getTokenResponses)
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }

    /**
     * Get a token
     * @param id ID of the token
     * @return A token
     */
    @Get("/{id}")
    fun getOneToken(id: UUID): HttpResponse<GetTokenResponse> {
        logger.debug("Called getOneToken($id)")
        return try {
            val token = tokenService.get(id)

            val getTokenResponse = GetTokenResponse(token)
            HttpResponse.ok(getTokenResponse)
        } catch (e: TokenService.ItemNotFoundException) {
            logger.debug { "Getting token $id failed: ${e.message}" }
            HttpResponse.notFound()
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }

    /**
     * Create a token.
     * @return A token with their ID
     */
    @Post("/")
    fun postOneToken(addTokenRequest: AddTokenRequest): HttpResponse<AddTokenResponse> {
        logger.debug("Called postOneToken($addTokenRequest)")

        return try {
            val token = addTokenRequest.toToken()
            val addedToken = tokenService.add(token)

            val addTokenResponse = AddTokenResponse(addedToken)
            HttpResponse.created(addTokenResponse, URI(addedToken.id.toString()))
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }

    /**
     * Update a token.
     * @param id ID of the token
     * @return The updated token
     */
    @Put("/{id}")
    fun putOneToken(id: UUID, updateTokenRequest: UpdateTokenRequest): HttpResponse<UpdateTokenResponse> {
        logger.debug("Called putOneToken($id, $updateTokenRequest)")

        return try {
            val token = updateTokenRequest.toToken()
            val updatedToken = tokenService.update(id, token)

            val updateTokenResponse = UpdateTokenResponse(updatedToken)
            HttpResponse.ok(updateTokenResponse)
        } catch (e: TokenService.ItemNotFoundException) {
            logger.debug { "Updating token $id failed: ${e.message}" }
            HttpResponse.notFound()
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }

    /**
     * Delete a token.
     * @param id ID of the token
     */
    @Delete("/{id}")
    fun deleteOneToken(id: UUID): HttpResponse<Unit> {
        logger.debug("Called deleteOneToken($id)")
        return try {
            tokenService.delete(id)

            HttpResponse.noContent()
        } catch (e: TokenService.ItemNotFoundException) {
            HttpResponse.notFound()
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }

    /**
     * Delete all token.
     */
    @Delete("/")
    fun deleteAllTokens(): HttpResponse<Unit> {
        logger.debug("Called deleteAllTokens()")
        return try {
            tokenService.deleteAll()

            HttpResponse.noContent()
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            HttpResponse.serverError()
        }
    }

    /**
     * Download a never ending file of random names
     */
    @Get("/endlessRandom")
    fun downloadRandomEndless(): StreamedFile {
        logger.debug("Called downloadRandomEndless()")

        val inputStream = tokenService.randomStream()
        return StreamedFile(inputStream, MediaType.TEXT_PLAIN_TYPE)
    }

    /**
     * Get all VIPs
     * @return All VIPs
     */
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/VIPs")
    fun getVIPs(): Set<GetTokenResponse> {
        logger.debug("Called getVIPs()")
        return setOf(
            GetTokenResponse(UUID.randomUUID(), "Harry Potter"),
            GetTokenResponse(UUID.randomUUID(), "Hermione Granger"),
            GetTokenResponse(UUID.randomUUID(), "Ronald Weasley")
        )
    }
}