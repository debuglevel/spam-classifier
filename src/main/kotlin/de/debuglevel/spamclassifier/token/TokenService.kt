package de.debuglevel.spamclassifier.token

import de.debuglevel.spamclassifier.person.PersonService
import jakarta.inject.Singleton
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.*

@Singleton
class TokenService(
    private val tokenRepository: TokenRepository,
) {
    private val logger = KotlinLogging.logger {}

    val count: Long
        get() {
            logger.debug { "Getting tokens count..." }

            val count = tokenRepository.count()

            logger.debug { "Got tokens count: $count" }
            return count
        }

//    fun exists(id: UUID): Boolean {
//        logger.debug { "Checking if token $id exists..." }
//
//        val isExisting = tokenRepository.existsById(id)
//
//        logger.debug { "Checked if token $id exists: $isExisting" }
//        return isExisting
//    }

    fun exists(name: String): Boolean {
        logger.debug { "Checking if token $name exists..." }

        val isExisting = tokenRepository.existsByText(name)

        logger.debug { "Checked if token $name exists: $isExisting" }
        return isExisting
    }

    private fun get(id: UUID): Token {
        logger.debug { "Getting token with ID '$id'..." }

        val token: Token = tokenRepository.findById(id).orElseThrow {
            logger.debug { "Getting token with ID '$id' failed" }
            ItemNotFoundException(id)
        }

        logger.debug { "Got token with ID '$id': $token" }
        return token
    }

    fun get(name: String): Token {
        logger.debug { "Getting token '$name'..." }

        val token: Token = tokenRepository.find(name).orElseThrow {
            logger.debug { "Getting token '$name' failed" }
            PersonService.ItemNotFoundException(name)
        }

        logger.debug { "Got token with ID '$name': $token" }
        return token
    }

//    fun getAll(): Set<Token> {
//        logger.debug { "Getting all tokens..." }
//
//        val tokens = tokenRepository.findAll().toSet()
//
//        logger.debug { "Got ${tokens.size} tokens" }
//        return tokens
//    }

    private fun add(token: Token): Token {
        logger.debug { "Adding token '$token'..." }

        val savedToken = tokenRepository.save(token)

        logger.debug { "Added token: $savedToken" }
        return savedToken
    }

    fun increase(
        text: String,
        spamClass: SpamClass,
        seenOn: LocalDateTime = LocalDateTime.now(),
        count: Int = 1
    ): Token {
        logger.debug { "Increasing token '$text' as $spamClass..." }

        val tokenExists = exists(text)
        val savedToken = if (tokenExists) {
            val gotToken = get(text)
            val updateToken = gotToken.copy(
                spamCount = when (spamClass) {
                    SpamClass.Spam -> gotToken.spamCount + count
                    else -> gotToken.spamCount
                },
                hamCount = when (spamClass) {
                    SpamClass.Ham -> gotToken.hamCount + count
                    else -> gotToken.hamCount
                },
                lastSeenOn = setOf(gotToken.lastSeenOn, seenOn).maxOf { it }
            )
            update(gotToken.id!!, updateToken)
        } else {
            val token = Token(
                id = null,
                text = text,
                spamCount = when (spamClass) {
                    SpamClass.Spam -> count
                    else -> 0
                },
                hamCount = when (spamClass) {
                    SpamClass.Ham -> count
                    else -> 0
                },
                lastSeenOn = seenOn,
            )
            add(token)
        }

        logger.debug { "Increased token: $savedToken as $spamClass" }
        return savedToken
    }

    private fun update(id: UUID, token: Token): Token {
        logger.debug { "Updating token '$token' with ID '$id'..." }

        // an object must be known to Hibernate (i.e. retrieved first) to get updated;
        // it would be a "detached entity" otherwise.
        val updateToken = this.get(id).apply {
            this.spamCount = token.spamCount
            this.hamCount = token.hamCount
            this.lastSeenOn = token.lastSeenOn
        }

        val updatedToken = tokenRepository.update(updateToken)

        logger.debug { "Updated token: $updatedToken with ID '$id'" }
        return updatedToken
    }

//    fun delete(id: UUID) {
//        logger.debug { "Deleting token with ID '$id'..." }
//
//        if (tokenRepository.existsById(id)) {
//            tokenRepository.deleteById(id)
//        } else {
//            throw ItemNotFoundException(id)
//        }
//
//        logger.debug { "Deleted token with ID '$id'" }
//    }

    fun deleteAll() {
        logger.debug { "Deleting all tokens..." }

        val countBefore = tokenRepository.count()
        tokenRepository.deleteAll() // CAVEAT: does not delete dependent entities; use this instead: tokenRepository.findAll().forEach { tokenRepository.delete(it) }
        val countAfter = tokenRepository.count()
        val countDeleted = countBefore - countAfter

        logger.debug { "Deleted $countDeleted of $countBefore tokens, $countAfter remaining" }
    }

    class ItemNotFoundException(criteria: Any) : Exception("Item '$criteria' does not exist.")
}