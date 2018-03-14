package io.corbs.csvkit

open class CsvLine(val line: String) {

    private val values = this.line.split(",").toList()

    fun asList(): List<String> {
        return this.values
    }

    override fun toString(): String {
        return this.line
    }
}