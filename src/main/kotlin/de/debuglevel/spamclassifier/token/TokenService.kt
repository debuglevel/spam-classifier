package de.debuglevel.spamclassifier.token

import jakarta.inject.Singleton
import mu.KotlinLogging
import java.io.InputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.util.*
import kotlin.concurrent.thread

@Singleton
class TokenService(
    private val tokenRepository: TokenRepository,
    private val tokenGeneratorService: TokenGeneratorService,
    private val tokenProperties: TokenProperties,
) {
    private val logger = KotlinLogging.logger {}

    val count: Long
        get() {
            logger.debug { "Getting tokens count..." }

            val count = tokenRepository.count()

            logger.debug { "Got tokens count: $count" }
            return count
        }

    fun exists(id: UUID): Boolean {
        logger.debug { "Checking if token $id exists..." }

        val isExisting = tokenRepository.existsById(id)

        logger.debug { "Checked if token $id exists: $isExisting" }
        return isExisting
    }

    fun get(id: UUID): Token {
        logger.debug { "Getting token with ID '$id'..." }

        val token: Token = tokenRepository.findById(id).orElseThrow {
            logger.debug { "Getting token with ID '$id' failed" }
            ItemNotFoundException(id)
        }

        logger.debug { "Got token with ID '$id': $token" }
        return token
    }

    fun getAll(): Set<Token> {
        logger.debug { "Getting all tokens..." }

        val tokens = tokenRepository.findAll().toSet()

        logger.debug { "Got ${tokens.size} tokens" }
        return tokens
    }

    fun add(token: Token): Token {
        logger.debug { "Adding token '$token'..." }

        val savedToken = tokenRepository.save(token)

        logger.debug { "Added token: $savedToken" }
        return savedToken
    }

    fun update(id: UUID, token: Token): Token {
        logger.debug { "Updating token '$token' with ID '$id'..." }

        // an object must be known to Hibernate (i.e. retrieved first) to get updated;
        // it would be a "detached entity" otherwise.
        val updateToken = this.get(id).apply {
            name = token.name
        }

        val updatedToken = tokenRepository.update(updateToken)

        logger.debug { "Updated token: $updatedToken with ID '$id'" }
        return updatedToken
    }

    fun delete(id: UUID) {
        logger.debug { "Deleting token with ID '$id'..." }

        if (tokenRepository.existsById(id)) {
            tokenRepository.deleteById(id)
        } else {
            throw ItemNotFoundException(id)
        }

        logger.debug { "Deleted token with ID '$id'" }
    }

    fun deleteAll() {
        logger.debug { "Deleting all tokens..." }

        val countBefore = tokenRepository.count()
        tokenRepository.deleteAll() // CAVEAT: does not delete dependent entities; use this instead: tokenRepository.findAll().forEach { tokenRepository.delete(it) }
        val countAfter = tokenRepository.count()
        val countDeleted = countBefore - countAfter

        logger.debug { "Deleted $countDeleted of $countBefore tokens, $countAfter remaining" }
    }

    fun randomStream(): InputStream {
        val outputStream = PipedOutputStream()

        val inputStream = PipedInputStream()
        inputStream.connect(outputStream)

        thread(start = true) {
            logger.debug { "Thread ${Thread.currentThread()} started." }

            var pipeOpen = true
            while (pipeOpen) {
                logger.debug { "Thread ${Thread.currentThread()} sleeping..." }
                Thread.sleep(500)

                val name = tokenGeneratorService.generateRandom().name
                val byteArray = "$name\n".toByteArray()

                try {
                    logger.debug { "Writing '$name' to OutputStream..." }
                    outputStream.write(byteArray)
                    logger.debug { "Wrote '$name' to OutputStream." }
                } catch (e: Exception) {
                    logger.warn(e) { "Writing to OutputStream failed" }
                    pipeOpen = false
                }
            }

            logger.debug { "Pipe was closed; Thread is ending..." }
        }

        return inputStream
    }

    class ItemNotFoundException(criteria: Any) : Exception("Item '$criteria' does not exist.")
}