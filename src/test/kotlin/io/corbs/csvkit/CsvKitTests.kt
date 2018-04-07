package io.corbs.csvkit

import com.fasterxml.jackson.databind.ObjectMapper
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.ObjectUtils
import java.io.StringWriter
import java.io.Writer


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CsvKitTests {

    companion object { val LOG = LoggerFactory.getLogger(CsvKitTests::class.java.name) }

    @Autowired
    private val client: TestRestTemplate? = null

    @Autowired
    private val json: ObjectMapper? = null

    @Test
    fun hasOpsHealth(){
        val response = client?.getForObject("/ops/health", String::class.java)
        val health = json?.readValue(response, Health::class.java)
        assertEquals(Health("UP"), health)
    }

    @Test
    fun createCsv() {
        val request = HttpEntity("one,fish\ntwo,fish\nred,fish\n,blue,fish\n")
        val response = client?.postForObject("/csv/createCsv", request, String::class.java)
        assertEquals("thank,you,for,4,lines", response)
    }

    @Value("\${csvkit.test.endpoint}")
    private val testEndpoint = ""

    @Value("\${csvkit.test.duration}")
    private val testDuration = 3000

    @Value("\${csvkit.test.numberOfLines}")
    private val testNumberOfLines = 13

    @Test
    fun doQueryTest() {
        doQueryTest(100)
    }

    fun doQueryTest(calm: Int = 1000) {

        // create 3 lines
        testCsvClient(3)

        var current = System.currentTimeMillis()
        val ending = current + testDuration
        while(current < ending) {

            // read them repeat for N milliseconds
            val response = if(ObjectUtils.isEmpty(testEndpoint)) {
                client?.getForObject("/csv/names", String::class.java)
            } else {
                client?.getForObject("${testEndpoint}/csv/names", String::class.java)
            }

            LOG.info(response)

            Thread.sleep(calm.toLong()) // calming effect
            current = System.currentTimeMillis()
        }
    }

    @Test
    fun testCsvClient() {
        testCsvClient(1, 100)
    }

    fun testCsvClient(numberOfLines: Int = 1, calm: Int = 100){
        var current = System.currentTimeMillis()
        val ending = current + testDuration
        while(current < ending) {

            // TODO make more idomatic
            val body = createLines(numberOfLines)
            val response = doTestRequest(body)

            LOG.info(response)

            Thread.sleep(calm.toLong()) // calming effect
            current = System.currentTimeMillis()
        }
    }

    private fun createLines(number: Int = 10): HttpEntity<String> {
        val writer = StringWriter()
        val n = RandomKit.getIntegerBetween(1, number)
        for(i in 1..n) {
            val line = mutableListOf<String>()
            for(j in 1..n) {
                line.add(RandomKit.firstName)
            }
            writeLine(writer, line)
        }

        return HttpEntity(writer.toString())
    }

    private fun doTestRequest(request: HttpEntity<String>): String {
        // you know this is handy :)
        val response = if(ObjectUtils.isEmpty(testEndpoint)) {
            client?.postForObject("/csv/names", request, String::class.java)
        } else {
            client?.postForObject("${testEndpoint}/csv/names", request, String::class.java)
        }

        return response ?: ""
    }

    private fun writeLine(writer: Writer, values: List<String>, customQuote: Char=' ') {
        var first = true

        val sb = StringBuilder()
        for (value in values) {
            if (!first) {
                sb.append(",")
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value))
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote)
            }

            first = false
        }
        sb.append("\n")
        writer.append(sb.toString())
    }

    private fun followCVSformat(value: String): String {

        var result = value
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"")
        }
        return result

    }
}

/**
 * POKO yolo
 */
data class Health(val status: String)
