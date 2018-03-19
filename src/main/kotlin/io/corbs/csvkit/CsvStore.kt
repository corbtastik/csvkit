package io.corbs.csvkit

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CsvStore(@Autowired val repo: CsvLineRepository) {

    val size = 100

    companion object {
        val LOG = LoggerFactory.getLogger(CsvKitAPI::class.java.name)
    }

    @HystrixCommand(fallbackMethod = "saveOffline")
    fun save(tag: String, list: List<CsvLine>) {
        val adapters = mutableListOf<CsvLineEntityAdapter>()
        for(csvLine in list) {
            adapters.add(CsvLineEntityAdapter(tag, csvLine.toString()))
        }
        repo.saveAll(adapters)
    }

    fun saveOffline(tag: String, list: List<CsvLine>) {
        LOG.info("tag: ${tag} list of ${list.size} csv lines can't be saved Grass Hopper")
    }

    fun findAll(tag: String, callback: (List<CsvLine>) -> Unit) {
        val buffer = mutableListOf<CsvLine>()
        var i = 1

        repo.findByTag(tag).forEach{
            if(i == 1) {
                buffer.add(CsvHeader(it.text))
            } else {
                buffer.add(CsvLine(it.text))
            }
            if((i % this.size) == 0) {
                callback(buffer)
                buffer.clear()
            }
            i++
        }
        callback(buffer)
    }

}

