package de.debuglevel.spamclassifier.text

import de.debuglevel.spamclassifier.token.SpamClass
import java.time.LocalDateTime

data class AddTextRequest(
    val text: String,
    val classification: SpamClass,
    val seenOn: LocalDateTime?,
)