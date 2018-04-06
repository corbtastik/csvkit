package io.corbs.csvkit

import com.fasterxml.jackson.databind.ObjectMapper
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForObject
import org.springframework.http.HttpEntity
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CsvKitTests {

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

}

/**
 * POKO yolo
 */
data class Health(val status: String)
