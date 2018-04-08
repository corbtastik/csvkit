package io.corbs.csvkit

import com.netflix.hystrix.HystrixCommandKey
import com.netflix.hystrix.HystrixEventType
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import com.netflix.hystrix.strategy.HystrixPlugins
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service

class CircuitAction : HystrixEventNotifier() {

    override fun markEvent(eventType: HystrixEventType, key: HystrixCommandKey) {
        CsvService.LOG.info("HystrixEventType.name=${eventType.name}")
    }
}

@Service
class CsvService(
    @Autowired val lineRepo: CsvLineRepository,
    @Autowired val metaRepo: CsvMetadataRepository,
    @Autowired val cacheManager: CacheManager) {

    companion object { val LOG = LoggerFactory.getLogger(CsvService::class.java.name) }

    init {
        HystrixPlugins.getInstance().registerEventNotifier(CircuitAction());
    }

    @HystrixCommand(fallbackMethod = "saveOffline")
    fun saveLines(tag: String, list: List<CsvLine>) {
        val adapters = mutableListOf<CsvLineEntity>()
        for(csvLine in list) {
            adapters.add(CsvLineEntity(tag, csvLine.toString()))
        }
        lineRepo.saveAll(adapters)
    }

    private fun saveOffline(tag: String, list: List<CsvLine>) {
        LOG.info("tag: $tag list of ${list.size} csv lines can't be saved so we're queuing offline")
        cacheManager.getCache("csv-lines-cache")?.put(tag, list)
    }

    @HystrixCommand(fallbackMethod = "findOffline")
    fun findLines(tag: String): List<CsvLine> {
        val lines = mutableListOf<CsvLine>()
        lineRepo.findByTagOrderById(tag).map {
            it -> CsvLine(it.text)
        }.toCollection(lines)

        return lines
    }

    private fun findOffline(tag: String): List<CsvLine> {
        LOG.info("tag: $tag cannot be fetched from repo, fetching from cache")

        return if(cacheManager.getCache("csv-lines-cache")?.get(tag)?.get() != null) {
            cacheManager.getCache("csv-lines-cache")?.get(tag)?.get() as List<CsvLine>
        } else {
            emptyList<CsvLine>()
        }
    }

    @HystrixCommand(fallbackMethod = "removeOffline")
    fun removeLines(tag: String): Int {
        val lines = lineRepo.findByTagOrderById(tag)
        lineRepo.deleteAll(lines)
        return lines.size
    }

    fun removeOffline(tag: String): Int {
        LOG.info("tag: $tag cannot be removed from repo, removing from cache")
        cacheManager.getCache("csv-lines-cache")?.evict(tag)
        return 1
    }

    @HystrixCommand(fallbackMethod = "saveMetadataOffline")
    fun saveMetadata(metadata: CsvMetadata) {
        metaRepo.save(metadata)
    }

    private fun saveMetadataOffline(metadata: CsvMetadata) {
        cacheManager.getCache("csv-meta-cache")?.put(metadata.tag,metadata)
    }

    @HystrixCommand(fallbackMethod = "findMetadataOffline")
    fun findMetadata(tag: String): List<CsvMetadata> {
        return metaRepo.findByTagOrderById(tag)
    }

    private fun findMetadataOffline(tag: String): List<CsvMetadata> {
        return if(cacheManager.getCache("csv-meta-cache")?.get(tag)?.get() != null) {
            cacheManager.getCache("csv-meta-cache")?.get(tag)?.get() as List<CsvMetadata>
        } else {
            emptyList<CsvMetadata>()
        }
    }

    @HystrixCommand(fallbackMethod = "removeMetadataOffline")
    fun removeMetadata(tag: String): Int {
        val recs = metaRepo.findByTagOrderById(tag)
        metaRepo.deleteAll(recs)
        return recs.size
    }

    private fun removeMetadataOffline(tag: String): Int {
        LOG.info("meta for tag: $tag cannot be removed from repo, removing from cache")
        cacheManager.getCache("csv-meta-cache")?.evict(tag)
        return 1
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

