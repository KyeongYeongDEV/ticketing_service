package com.example.ticketing_service

import io.github.cdimascio.dotenv.dotenv
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
class TicketingServiceApplication

fun main(args: Array<String>) {
    val dotenv = dotenv {
        ignoreIfMissing   = true
    }

    dotenv.entries().forEach { entry ->
        System.setProperty(entry.key, entry.value)
    }

	runApplication<TicketingServiceApplication>(*args)
}
