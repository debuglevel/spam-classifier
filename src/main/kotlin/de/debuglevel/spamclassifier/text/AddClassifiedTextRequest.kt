package de.debuglevel.spamclassifier.text

import de.debuglevel.spamclassifier.token.Category
import java.time.LocalDateTime

data class AddClassifiedTextRequest(
    val text: String,
    val classification: Category,
    val seenOn: LocalDateTime?,
)