package de.debuglevel.spamclassifier.classifier.opennlp

import opennlp.tools.util.InputStreamFactory
import java.io.InputStream

class AnyInputStreamFactory(private val inputStream: InputStream) : InputStreamFactory {
    private var tainted = false

    override fun createInputStream(): InputStream {
        return if (!tainted) {
            tainted = true
            inputStream
        } else {
            throw UnsupportedOperationException("Stream can't be re-created to read from the beginning!")
        }
    }
}