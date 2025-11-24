package com.example.demo

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

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
