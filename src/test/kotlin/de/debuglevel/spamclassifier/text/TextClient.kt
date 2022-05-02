package de.debuglevel.spamclassifier.text

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import reactor.core.publisher.Mono

@Client("/texts")
interface TextClient {
    @Post("/")
    fun add(@Body addTextRequest: AddTextRequest): Mono<Unit>
}