package de.debuglevel.spamclassifier.token

import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.constraints.NotBlank

@Client("/tokens")
interface TokenClient {
    @Get("/{id}")
    fun get(@NotBlank id: UUID): Mono<GetTokenResponse>

    // TODO: Should probably be a reactive Flux<> instead
    @Get("/")
    fun getAll(): List<GetTokenResponse>

    @Post("/")
    fun add(@Body token: AddTokenRequest): Mono<AddTokenResponse>

    @Put("/{id}")
    fun update(@NotBlank id: UUID, @Body token: UpdateTokenRequest): Mono<UpdateTokenResponse>

    @Get("/VIPs")
    fun getVIPs(@Header authorization: String): Set<GetTokenResponse>
}