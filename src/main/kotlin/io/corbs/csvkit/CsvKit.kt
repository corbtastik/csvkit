package io.corbs.csvkit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker

@EnableCircuitBreaker
@SpringBootApplication
class CsvKit

fun main(args: Array<String>) {
    runApplication<CsvKit>(*args)
}
