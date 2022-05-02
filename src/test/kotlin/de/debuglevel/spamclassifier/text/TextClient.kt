package de.debuglevel.spamclassifier.text

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client("/texts")
interface TextClient {
    @Post("/classified/")
    fun addClassified(@Body addClassifiedTextRequest: AddClassifiedTextRequest): HttpResponse<Unit>

    @Post("/unclassified/")
    fun addUnclassified(@Body addUnclassifiedTextRequest: AddUnclassifiedTextRequest): HttpResponse<AddUnclassifiedTextResponse>
}