package ru.bicubictwice.springwebfluxshortlinksservice.schedule

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.bicubictwice.springwebfluxshortlinksservice.model.Status
import ru.bicubictwice.springwebfluxshortlinksservice.repository.LinksRepository
import ru.bicubictwice.springwebfluxshortlinksservice.repository.MetricsRepository
import java.time.LocalDateTime

@Component
class InactivateUnusedLinks(
        @Autowired env: Environment,
        @Autowired val linksRepo: LinksRepository,
        @Autowired val metricsRepo: MetricsRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private var inactiveAfterDays = env.getRequiredProperty("limits.inactive-after-days").toLong()

    @Scheduled(cron = "0 0 2 * * *") // at 2:00 AM daily
    fun scheduled() {
        val timeFrom = LocalDateTime.now().minusDays(inactiveAfterDays)
        inactivateUnusedLinks(timeFrom)
    }

    @Transactional(readOnly = false)
    fun inactivateUnusedLinks(timeFrom: LocalDateTime) {
        metricsRepo.findByLastUsedLessThan(timeFrom).forEach {
            val link = it.link!!
            link.status = Status.NOT_ACTIVE
            linksRepo.save(link)
        }
    }
}
