package com.matthewcasperson.webflux

import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.logging.LogFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration.ofSeconds
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

@SpringBootApplication
class ServiceApplication

fun main() {
	runApplication<ServiceApplication>()
}

@RestController
class CarRegoRestController(private val carRegoService: CarRegoService) {

	@GetMapping(value = ["/cars"],
		produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
	fun prices() = carRegoService.streamOfCars()
}

@Controller
class CarRegoRSocketController(private val carRegoService: CarRegoService) {

	@MessageMapping("cars")
	fun cars(symbol: String) = carRegoService.streamOfCars()
}

@Service
class CarRegoService {
	private val log = LogFactory.getLog(javaClass)

	fun streamOfCars(): Flux<Car> {
		return Flux
			.interval(ofSeconds(1))
			.map { Car(RandomStringUtils.random(6), "image" + Random.nextInt(0, 5) + ".png") }
			.doOnSubscribe { log.info("New subscription") }
			.share()
	}
}

data class Car(val rego: String, val image: String)