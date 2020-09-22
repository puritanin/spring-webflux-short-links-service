package ru.bicubictwice.springwebfluxshortlinksservice.service

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.bicubictwice.springwebfluxshortlinksservice.data.model.Status
import ru.bicubictwice.springwebfluxshortlinksservice.data.repository.LinksRepository
import ru.bicubictwice.springwebfluxshortlinksservice.data.repository.MetricsRepository
import java.time.LocalDateTime
import javax.annotation.PreDestroy

@Service
class TranslationService(
        @Autowired val linksRepo: LinksRepository,
        @Autowired val metricsRepo: MetricsRepository,
        @Autowired val utils: Utils
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { _, e ->
        log.error("IN - CoroutineScope exception: ${e.message}", e)
    })

    @PreDestroy
    private fun onDestroy() = ioScope.cancel()

    fun translate(shortUri: String): String {
        val link = findByShortUri(shortUri)
        log.info("IN getRedirectUrl - found: $link")
        return if (link != null && link.status == Status.ACTIVE) {
            ioScope.launch {
                updateLastUsedTimestamp(link.id!!)
            }
            link.redirectUrl!!
        } else utils.makeUrlFromSelf()
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ, readOnly = false)
    fun updateLastUsedTimestamp(linkId: Long) {
        linksRepo.findOneWithMetricsById(linkId)?.let { link ->
            val metrics = link.metrics!!
            metrics.lastUsed = LocalDateTime.now()
            metricsRepo.save(metrics)
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    fun findByShortUri(uri: String) = linksRepo.findByShortUri(utils.formatUriForDatabase(uri))
}
