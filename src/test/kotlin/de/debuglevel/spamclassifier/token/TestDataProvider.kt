package de.debuglevel.spamclassifier.token

import java.time.LocalDateTime
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

    data class TokenTestData(
        val text: String,
        val category: Category,
        val seenOn: LocalDateTime,
    )

    fun tokenProvider() = Stream.of(
        TokenTestData(
            text = "Harry",
            category = Category.Ham,
            seenOn = LocalDateTime.now(),
        ),
        TokenTestData(
            text = "Ron",
            category = Category.Ham,
            seenOn = LocalDateTime.now(),
        ),
        TokenTestData(
            text = "Albus",
            category = Category.Ham,
            seenOn = LocalDateTime.now(),
        ),
        TokenTestData(
            text = "Severus",
            category = Category.Ham,
            seenOn = LocalDateTime.now(),
        ),
        TokenTestData(
            text = "Severus",
            category = Category.Spam,
            seenOn = LocalDateTime.now(),
        ),
        TokenTestData(
            text = "Voldemort",
            category = Category.Spam,
            seenOn = LocalDateTime.now(),
        ),
        TokenTestData(
            text = "Lucius",
            category = Category.Spam,
            seenOn = LocalDateTime.now(),
        ),
    )
}