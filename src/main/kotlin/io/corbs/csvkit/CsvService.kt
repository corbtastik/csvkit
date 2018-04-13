package io.corbs.csvkit

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CsvService(@Autowired val repo: CsvLineRepository) {

    companion object { val LOG = LoggerFactory.getLogger(CsvService::class.java.name) }

    @HystrixCommand(fallbackMethod = "saveOffline")
    fun saveLines(tag: String, list: List<CsvLine>) {
        val adapters = mutableListOf<CsvLineEntity>()
        for(csvLine in list) {
            adapters.add(CsvLineEntity(tag, csvLine.toString()))
        }
        repo.saveAll(adapters)
    }

    fun saveOffline(tag: String, list: List<CsvLine>) {
        LOG.info("tag: ${tag} list of ${list.size} csv lines can't be saved Grass Hopper")
    }

    fun findLines(tag: String): List<CsvLine> {
        val lines = mutableListOf<CsvLine>()
        repo.findByTagOrderById(tag).map {
            it -> CsvLine(it.text)
        }.toCollection(lines)

        return lines
    }

    fun removeLines(tag: String): Int {
        val lines = repo.findByTagOrderById(tag)
        repo.deleteAll(lines)
        return lines.size
    }



}

