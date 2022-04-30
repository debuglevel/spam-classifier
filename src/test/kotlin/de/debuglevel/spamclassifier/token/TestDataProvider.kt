package de.debuglevel.spamclassifier.token

import java.util.stream.Stream

object TestDataProvider {
    data class TokenTestData(
        val text: String,
        val tokens: List<String>,
    )

    fun textProvider() = Stream.of(
        TokenTestData(
            text = "Hello",
            tokens = listOf("hello")
        ),
        TokenTestData(
            text = "Hello, my name is Harry.",
            tokens = listOf("hello", "my", "name", "is", "harry")
        ),
        TokenTestData(
            text = "Hello    there",
            tokens = listOf("hello", "there")
        ),
        TokenTestData(
            text = "Hello?!?!? Anybody here?!!",
            tokens = listOf("hello", "anybody", "here")
        ),
    )
}