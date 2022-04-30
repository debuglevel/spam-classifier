package de.debuglevel.spamclassifier.token

import java.util.stream.Stream

object TestDataProvider {
    data class TextTestData(
        val text: String,
        val tokens: List<String>,
    )

    fun textProvider() = Stream.of(
        TextTestData(
            text = "Hello",
            tokens = listOf("hello")
        ),
        TextTestData(
            text = "Hello, hello",
            tokens = listOf("hello", "hello")
        ),
        TextTestData(
            text = "Hello, my name is Harry.",
            tokens = listOf("hello", "my", "name", "is", "harry")
        ),
        TextTestData(
            text = "Hello    there",
            tokens = listOf("hello", "there")
        ),
        TextTestData(
            text = "Hello?!?!? Anybody here?!!",
            tokens = listOf("hello", "anybody", "here")
        ),
    )
}