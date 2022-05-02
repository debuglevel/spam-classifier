package de.debuglevel.spamclassifier.text

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client("/texts")
interface TextClient {
    @Post("/classified/")
    fun addClassified(@Body addTextRequest: AddTextRequest): HttpResponse<Unit>
}