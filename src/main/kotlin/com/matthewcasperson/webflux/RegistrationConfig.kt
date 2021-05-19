package com.matthewcasperson.webflux

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Flux
import kotlin.random.Random
import kotlin.streams.asStream

@Configuration
class RegistrationConfig {
    @Bean
    fun getCar(): RouterFunction<ServerResponse> {
        return route(
            GET("/cars")
        ) { req ->
            ok().body(
                getCarsFlux(), Car::class.java
            )
        }
    }

    private fun getCarsFlux() = Flux.fromStream(getCars().asStream())

    private fun getCars(): Sequence<Car> = sequence {
        while (true) {
            yield(Car(
                RandomStringUtils.randomAlphanumeric(6),
                "image" + Random.nextInt(5) + ".png"))
        }
    }
}