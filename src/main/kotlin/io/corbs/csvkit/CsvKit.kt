package io.corbs.csvkit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CsvKit

fun main(args: Array<String>) {
    runApplication<CsvKit>(*args)
}
