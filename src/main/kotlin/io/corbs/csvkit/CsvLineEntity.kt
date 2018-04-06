package io.corbs.csvkit

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name = "csv_lines")
data class CsvLineEntity(val tag: String, val text: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
    constructor() : this("","")
}