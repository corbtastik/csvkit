package io.corbs.csvkit

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CsvLineRepository : CrudRepository<CsvLineEntity, String> {
    fun findByTagOrderById(tag:String): List<CsvLineEntity>
}