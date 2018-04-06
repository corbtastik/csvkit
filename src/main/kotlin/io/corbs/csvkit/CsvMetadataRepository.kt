package io.corbs.csvkit

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CsvMetadataRepository : CrudRepository<CsvMetadata, Long> {
    fun findByTagOrderById(tag:String): List<CsvMetadata>
}