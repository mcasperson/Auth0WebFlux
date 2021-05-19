package com.matthewcasperson.webflux

import org.springframework.web.bind.annotation.GetMapping
import reactor.core.publisher.Flux
import org.apache.commons.lang3.RandomStringUtils
import kotlin.random.Random
import kotlin.streams.asStream

class RegistrationController {

    @GetMapping("/")
    fun findAll(): Flux<Car> =
        Flux.fromStream<Car>(getCars().asStream())

    fun getCars(): Sequence<Car> = sequence {
        while (true) {
            yield(Car(
                RandomStringUtils.randomAlphanumeric(6),
                "image" + Random.nextInt(5) + ".png"))
        }
    }
}