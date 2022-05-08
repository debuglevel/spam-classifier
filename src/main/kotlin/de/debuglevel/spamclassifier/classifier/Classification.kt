package de.debuglevel.spamclassifier.classifier

import de.debuglevel.spamclassifier.token.Category

data class Classification(
    val category: Category,
    val probability: Double,
)
