package ru.bicubictwice.springwebfluxshortlinksservice.rest

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class HeadersWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        with(exchange.response.headers) {
            add("Cache-Control", "no-cache, no-store, must-revalidate")
            add("Pragma", "no-cache")
            add("Expires", "0")
        }
        return chain.filter(exchange)
    }
}
