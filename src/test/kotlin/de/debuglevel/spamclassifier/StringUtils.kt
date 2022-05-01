package de.debuglevel.spamclassifier

import kotlin.streams.asSequence

object StringUtils {
    fun createRandomString(length: Long): String {
        val source = "abcdefghijklmnopqrstuvwxyz"
        return java.util.Random().ints(length, 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
    }
}