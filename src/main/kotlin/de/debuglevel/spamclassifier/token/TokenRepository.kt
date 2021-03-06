package de.debuglevel.spamclassifier.token

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.util.*

@Repository
interface TokenRepository : CrudRepository<Token, UUID> {
    fun find(text: String): Optional<Token>
    fun existsByText(text: String): Boolean
}