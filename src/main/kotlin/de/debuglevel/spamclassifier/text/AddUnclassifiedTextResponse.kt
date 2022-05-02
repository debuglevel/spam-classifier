package de.debuglevel.spamclassifier.text

data class AddUnclassifiedTextResponse(
    val scores: Map<String, Double>,
) {
//    constructor(person: Person) : this(
//        person.id!!,
//        person.name,
//    )
}