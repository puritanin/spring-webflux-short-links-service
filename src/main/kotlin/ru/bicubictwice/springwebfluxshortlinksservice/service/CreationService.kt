package ru.bicubictwice.springwebfluxshortlinksservice.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.bicubictwice.springwebfluxshortlinksservice.data.model.Link
import ru.bicubictwice.springwebfluxshortlinksservice.data.model.Metrics
import ru.bicubictwice.springwebfluxshortlinksservice.data.model.Status
import ru.bicubictwice.springwebfluxshortlinksservice.data.repository.LinksRepository
import ru.bicubictwice.springwebfluxshortlinksservice.service.dto.LinkDto
import ru.bicubictwice.springwebfluxshortlinksservice.service.dto.LinkDtoMapper
import ru.bicubictwice.springwebfluxshortlinksservice.service.generator.UriGenerator
import java.time.LocalDateTime

@Service
class CreationService(
        @Autowired val linksRepo: LinksRepository,
        @Autowired val uriGen: UriGenerator,
        @Autowired val dtoMapper: LinkDtoMapper,
        @Autowired val utils: Utils
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    init {
        synchronizeSequenceValue()
    }

    private val nextSequenceShortUri: String
        get() = utils.formatUriForDatabase(uriGen.nextValue)

    private fun synchronizeSequenceValue() {
        val lastSeqValueLink = linksRepo.findTopByOrderByShortUriDesc()
        log.info("IN synchronizeSequenceValue - last seq value: ${lastSeqValueLink?.shortUri?.trim()}")
        lastSeqValueLink?.let { uriGen.currentValue = it.shortUri!!.trim() }
    }

    fun create(redirectUrl: String): LinkDto {
        val link = createLink(redirectUrl)
        return dtoMapper.toDto(link)
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ, readOnly = false)
    fun createLink(redirectUrl: String): Link {
        // если уже есть такая ссылка...
        linksRepo.findByRedirectUrl(redirectUrl)?.let { link ->
            if (link.status == Status.NOT_ACTIVE) {
                // ... и она не активна, активируем ее
                link.status = Status.ACTIVE
                linksRepo.save(link)
            }
            // ... возвращаем ее короткую ссылку
            log.info("IN createShortUri - used exist: $link")
            return link
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
        return link
    }
}
