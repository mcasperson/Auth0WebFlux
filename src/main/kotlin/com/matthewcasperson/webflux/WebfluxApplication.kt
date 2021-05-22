package com.matthewcasperson.webflux

import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.logging.LogFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.Duration.ofSeconds
import kotlin.random.Random

@SpringBootApplication
class ServiceApplication

fun main() {
	runApplication<ServiceApplication>()
}

@Controller
class CarRegoRSocketController(private val carRegoService: CarRegoService) {

	@MessageMapping("cars")
	fun cars() = carRegoService.streamOfCars()
}

@Service
class CarRegoService {
	private val log = LogFactory.getLog(javaClass)

	fun streamOfCars(): Flux<Car> {
		return Flux
			.interval(ofSeconds(1))
			.map { Car(
				RandomStringUtils.randomAlphabetic(6),
				"image" + Random.nextInt(1, 5) + ".png") }
			.doOnSubscribe { log.info("New subscription") }
			.share()
	}
}

data class Car(val rego: String, val image: String)