package io.corbs.csvkit

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CsvService(
    @Autowired val lineRepo: CsvLineRepository,
    @Autowired val metaRepo: CsvMetadataRepository) {

    companion object { val LOG = LoggerFactory.getLogger(CsvService::class.java.name) }

    @HystrixCommand(fallbackMethod = "saveOffline")
    fun saveLines(tag: String, list: List<CsvLine>) {
        val adapters = mutableListOf<CsvLineEntity>()
        for(csvLine in list) {
            adapters.add(CsvLineEntity(tag, csvLine.toString()))
        }
        lineRepo.saveAll(adapters)
    }

    fun saveOffline(tag: String, list: List<CsvLine>) {
        LOG.info("tag: ${tag} list of ${list.size} csv lines can't be saved Grass Hopper")
    }

    fun findLines(tag: String): List<CsvLine> {
        val lines = mutableListOf<CsvLine>()
        lineRepo.findByTagOrderById(tag).map {
            it -> CsvLine(it.text)
        }.toCollection(lines)

        return lines
    }

    fun removeLines(tag: String): Int {
        val lines = lineRepo.findByTagOrderById(tag)
        lineRepo.deleteAll(lines)
        return lines.size
    }

    fun saveMetadata(metadata: CsvMetadata) {
        metaRepo.save(metadata)
    }

    fun findMetadata(tag: String): List<CsvMetadata> {
        return metaRepo.findByTagOrderById(tag)
    }

    fun removeMetadata(tag: String): Int {
        val recs = metaRepo.findByTagOrderById(tag)
        metaRepo.deleteAll(recs)
        return recs.size
    }

//    fun findAllLines(tag: String, callback: (List<CsvLine>) -> Unit) {
//        val buffer = mutableListOf<CsvLine>()
//        var i = 1
//
//        repo.findByTag(tag).forEach{
//            if(i == 1) {
//                buffer.add(CsvHeader(it.text))
//            } else {
//                buffer.add(CsvLine(it.text))
//            }
//            if((i % this.size) == 0) {
//                callback(buffer)
//                buffer.clear()
//            }
//            i++
//        }
//        callback(buffer)
//    }

}

