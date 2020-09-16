package ru.bicubictwice.springwebfluxshortlinksservice.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.bicubictwice.springwebfluxshortlinksservice.model.Link

interface LinksRepository : JpaRepository<Link, Long> {

    fun findByShortUri(shortUri: String): Link?

    fun findByRedirectUrl(redirectUrl: String): Link?

    fun findTopByOrderByShortUriDesc(): Link?

    @Query("SELECT l FROM Link l LEFT JOIN FETCH l.metrics WHERE l.id=:id")
    fun findOneWithMetricsById(@Param("id") id: Long): Link?
}
