package io.corbs.csvkit

import org.slf4j.LoggerFactory
import java.io.InputStream

class CsvIO(val input: InputStream) {

    companion object {
        val LOG = LoggerFactory.getLogger(CsvIO::class.java.name)
    }

    var size = 100

    fun every(size: Int): CsvIO {
        this.size = size
        return this
    }

    fun go(batch: (List<CsvLine>) -> Unit) {
        LOG.debug("Starting go batch")
        var i = 1
        val buffer = mutableListOf<CsvLine>()
        val reader = input.bufferedReader()

        reader.lines().forEach {
            if(i == 1) {
                val header = it.trim()
                LOG.debug("Reading Header")
                LOG.debug(header)
                buffer.add(CsvHeader(header))
            } else {
                buffer.add(CsvLine(it.trim()))
                if (i % size == 0) {
                    LOG.debug("callback invoked with $size lines")
                    batch(buffer)
                    buffer.clear()
                }
            }
            i++
        }

        if(!buffer.isEmpty()) {
            LOG.debug("callback invoked with ${buffer.size} lines")
            batch(buffer)
        }

        LOG.debug("clearing buffer and closing reader")
        buffer.clear()
        reader.close()
    }
}



