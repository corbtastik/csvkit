package io.corbs.csvkit

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository("repo")
interface CsvLineRepository : CrudRepository<CsvLineEntityAdapter, String> {
    fun findByTag(tag:String): List<CsvLineEntityAdapter>
}