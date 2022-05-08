package de.debuglevel.spamclassifier.text

import de.debuglevel.spamclassifier.token.Category

data class AddUnclassifiedTextResponse(
    //val scores: Map<String, Double>,
    val scores: Map<String, Map<Category, Double>>,
) {
//    constructor(person: Person) : this(
//        person.id!!,
//        person.name,
//    )
}