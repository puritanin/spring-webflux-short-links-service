package ru.bicubictwice.springwebfluxshortlinksservice.data.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.bicubictwice.springwebfluxshortlinksservice.data.model.Metrics
import java.time.LocalDateTime

interface MetricsRepository : JpaRepository<Metrics, Long> {
    fun findByLastUsedLessThan(date: LocalDateTime): List<Metrics>
}
