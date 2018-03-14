package io.corbs.csvkit

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity(name = "csv_lines")
data class CsvLineEntityAdapter(val tag: String, val text: String) {
    @Id
    @GeneratedValue
    val id: Long? = null
    constructor() : this("","")
}