package ru.bicubictwice.springwebfluxshortlinksservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class SpringWebfluxShortLinksServiceApplication

fun main(args: Array<String>) {
	runApplication<SpringWebfluxShortLinksServiceApplication>(*args)
}
