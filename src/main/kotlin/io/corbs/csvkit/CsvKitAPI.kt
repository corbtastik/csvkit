package io.corbs.csvkit

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.BufferedReader
import java.io.StringReader
import java.util.stream.Collectors

@RestController
class CsvKitAPI(@Autowired val csvService: CsvService) {

    companion object { val LOG = LoggerFactory.getLogger(CsvKitAPI::class.java.name) }

    @PostMapping("/csv/{tag}")
    fun createCsv(@PathVariable("tag") tag: String, @RequestBody body: String): ResponseEntity<String> {
        val reader = BufferedReader(StringReader(body))
        val csvLines = reader.lines()
            .filter{it -> "" != it.trim() }
                .map {it -> CsvLine(it)}.collect(Collectors.toList())
        csvService.saveLines(tag, csvLines)
        val message = "thank,you,for,${csvLines.size},lines"
        LOG.info(message)
        return ResponseEntity(message, HttpStatus.CREATED)
    }

    @GetMapping("/csv/{tag}")
    fun readCsv(@PathVariable("tag") tag: String): String {
        val lines = csvService.findLines(tag).map { it -> it.line }
        val body = lines.stream().collect(Collectors.joining(System.lineSeparator()))
        LOG.info("returning,${lines.size},yummy,lines,under,tag,${tag}")
        return body
    }

    @DeleteMapping("/csv/{tag}")
    fun removeCsv(@PathVariable("tag") tag: String): ResponseEntity<String> {
        LOG.info("removing,tag,${tag}")
        return ResponseEntity("${csvService.removeLines(tag)} removed", HttpStatus.OK)
    }

}
