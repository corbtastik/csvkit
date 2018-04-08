package io.corbs.csvkit

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.cloud.netflix.hystrix.EnableHystrix
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard
import org.springframework.context.annotation.Bean
import java.net.InetAddress

@EnableHystrix
@EnableHystrixDashboard
@EnableCircuitBreaker
@SpringBootApplication
class CsvKit {

    companion object { val LOG = LoggerFactory.getLogger(CsvKit::class.java.name) }

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = SimpleCacheManager()
        cacheManager.setCaches(listOf(
                ConcurrentMapCache("csv-meta-cache"),
                ConcurrentMapCache("csv-lines-cache")))
        return cacheManager
    }
}

fun main(args: Array<String>) {

    val env = runApplication<CsvKit>(*args).environment
    var protocol = "http"
    if (env.getProperty("server.ssl.key-store") != null) {
        protocol = "https"
    }
    CsvKit.LOG.info("\n<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>\n\t" +
        "Application '{}' is running! Access URLs:\n\t" +
        "Local: \t\t{}://localhost:{}\n\t" +
        "External: \t{}://{}:{}\n\t" +
        "Profile(s): \t{}\n<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>",
        env.getProperty("spring.application.name"),
        protocol,
        env.getProperty("server.port"),
        protocol,
        InetAddress.getLocalHost().hostAddress,
        env.getProperty("server.port"),
        env.activeProfiles)

}


