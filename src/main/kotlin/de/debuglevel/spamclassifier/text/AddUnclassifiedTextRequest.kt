package de.debuglevel.spamclassifier.text

import java.time.LocalDateTime

data class AddUnclassifiedTextRequest(
    val text: String,
    val seenOn: LocalDateTime?,
)