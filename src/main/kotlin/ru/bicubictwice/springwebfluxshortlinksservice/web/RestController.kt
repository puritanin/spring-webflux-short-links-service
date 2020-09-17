package ru.bicubictwice.springwebfluxshortlinksservice.web

import kotlinx.coroutines.delay
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.reactive.result.view.RedirectView
import org.springframework.web.reactive.result.view.View
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import ru.bicubictwice.springwebfluxshortlinksservice.service.LinksService

@Controller
class RestController(@Autowired val service: LinksService) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/create",
            consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
            produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    suspend fun create(exchange: ServerWebExchange): Map<String, String> {
        val url = exchange.formData.awaitSingle()["data"]?.first()?.trim()
        log.info("IN create - POST data: $url")
        delay(1500L) // suspend, but not blocking
        return if (url != null && url.isNotBlank() && url.length <= 2000) {
            mapOf("short" to service.createRedirectUrl(url))
        } else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/{uri}")
    suspend fun redirect(@PathVariable uri: String): View {
        log.info("IN redirect - GET /$uri")
        val redirectUrl = service.getRedirectUrl(uri)
        log.info("IN redirect - go from $uri to $redirectUrl")
        return RedirectView(redirectUrl, HttpStatus.PERMANENT_REDIRECT)
    }
}
