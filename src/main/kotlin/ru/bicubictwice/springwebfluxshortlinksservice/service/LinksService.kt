package ru.bicubictwice.springwebfluxshortlinksservice.service

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.bicubictwice.springwebfluxshortlinksservice.model.Link
import ru.bicubictwice.springwebfluxshortlinksservice.model.Metrics
import ru.bicubictwice.springwebfluxshortlinksservice.model.Status
import ru.bicubictwice.springwebfluxshortlinksservice.repository.LinksRepository
import ru.bicubictwice.springwebfluxshortlinksservice.repository.MetricsRepository
import java.time.LocalDateTime
import javax.annotation.PreDestroy

@Service
class LinksService(
        @Autowired val linksRepo: LinksRepository,
        @Autowired val metricsRepo: MetricsRepository,
        @Autowired val seqGen: SequentialUriGenerator,
        @Autowired val utils: UrlUtils
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { _, e ->
        log.error("IN - CoroutineScope exception: ${e.message}", e)
    })


    init {
        synchronizeSequenceValue()
    }

    @PreDestroy
    private fun onDestroy() = ioScope.cancel()


    private val nextSequenceShortUri: String
        get() = formatUriForDatabase(seqGen.nextValue)

    private fun formatUriForDatabase(uri: String) = uri.padStart(10, ' ')


    private fun synchronizeSequenceValue() {
        val lastSeqValueLink = linksRepo.findTopByOrderByShortUriDesc()
        log.info("IN synchronizeSequenceValue - last seq value: ${lastSeqValueLink?.shortUri?.trim()}")
        lastSeqValueLink?.let { seqGen.currentValue = it.shortUri!!.trim() }
    }

    fun createRedirectUrl(redirectUrl: String): String {
        val uri = createShortUri(redirectUrl)
        return utils.makeUrlFromSelf(uri.trim())
    }

    fun getRedirectUrl(shortUri: String): String {
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
    fun createShortUri(redirectUrl: String): String {
        // если уже есть такая ссылка...
        linksRepo.findByRedirectUrl(redirectUrl)?.let { link ->
            if (link.status == Status.NOT_ACTIVE) {
                // ... и она не активна, активируем ее
                link.status = Status.ACTIVE
                linksRepo.save(link)
            }
            // ... возвращаем ее короткую ссылку
            log.info("IN createShortUri - used exist: $link")
            return link.shortUri!!
        }

        // если ссылки не оказалось в БД, создаем новую
        val link = Link().apply {
            this.redirectUrl = redirectUrl
            this.shortUri = nextSequenceShortUri
            this.status = Status.ACTIVE
            this.metrics = Metrics().apply {
                this.created = LocalDateTime.now()
                this.lastUsed = this.created
            }
        }
        linksRepo.save(link)
        log.info("IN createShortUri - created new: $link")
        return link.shortUri!!
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
    fun findByShortUri(uri: String) = linksRepo.findByShortUri(formatUriForDatabase(uri))
}
