package io.corbs.csvkit

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.io.BufferedOutputStream
import javax.servlet.http.HttpServletRequest

@RestController
class CsvKitAPI(@Autowired val csvStore: CsvStore) {

    companion object {
        val LOG = LoggerFactory.getLogger(CsvKitAPI::class.java.name)
    }

    /**
     * Note since we make 'size' optional we must also make the type Nullable
     * since Spring will pass a null value for optional types
     *
     * Take a csv file (header is assumed) and save under the client provided 'tag'
     * 'tag' must be a unique String. If such a 'tag' exists then an exception
     * will be raised.  If not the csv file is saved under the given 'tag' and performing
     * a GET on /csv/{tag} will stream out the csv data
     */
    @PostMapping("/csv/upload/{tag}")
    fun csvFile(@RequestParam("file") csv: MultipartFile,
                @PathVariable("tag") tag: String,
                @RequestParam("size", required = false) size: Int?) : String {

        if(csv.isEmpty) {
            return "file,is,empty"
        }

        CsvIO(csv.inputStream).every(1000).go { csvStore.save(tag, it) }

        return "howdy,yo,thank,you,for,tag,$tag,yummy,yum,yum"
    }

    @PostMapping("/csv/{tag}")
    fun csvBody(@PathVariable("tag") tag: String, request: HttpServletRequest) : String {

        if(request.inputStream == null) {
            return "inputstream,is,null"
        }

        CsvIO(request.inputStream).every(1000).go { csvStore.save(tag, it) }

        return "howdy,yo,thank,you,for,tag,$tag,yummy,yum,yum"
    }

    @GetMapping("/csv/{tag}")
    fun csvRead(@PathVariable("tag") tag: String, request: HttpServletRequest): StreamingResponseBody {
        return StreamingResponseBody {output -> run {
            val writer = BufferedOutputStream(output).bufferedWriter()
            csvStore.findAll(tag, {
                    for(line in it) {
                        writer.write(line.line + System.lineSeparator())
                    }
                    writer.flush()
                })
            }
        }
    }
}
