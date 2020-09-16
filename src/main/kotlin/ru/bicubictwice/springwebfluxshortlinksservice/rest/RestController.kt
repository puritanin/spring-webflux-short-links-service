package ru.bicubictwice.springwebfluxshortlinksservice.rest

import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.result.view.RedirectView
import org.springframework.web.reactive.result.view.View
import ru.bicubictwice.springwebfluxshortlinksservice.service.LinksService

@Controller
class RestController(@Autowired val service: LinksService) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/create", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    suspend fun create(@RequestParam(value = "url") redirectUrl: String): Any {
        delay(1500L) // suspend, but not blocking
        val url = service.createRedirectUrl(redirectUrl)
        return mapOf("short" to url)
    }

    @GetMapping("/{uri}")
    suspend fun redirect(@PathVariable uri: String): View {
        val redirectUrl = service.getRedirectUrl(uri)
        log.info("IN redirect - go from $uri to $redirectUrl")
        return RedirectView(redirectUrl, HttpStatus.PERMANENT_REDIRECT)
    }
}
