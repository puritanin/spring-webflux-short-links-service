package ru.bicubictwice.springwebfluxshortlinksservice.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class Utils(@Autowired env: Environment) {

    private var proto = env.getRequiredProperty("redirect.protocol").toLowerCase()
    private var host = env.getRequiredProperty("redirect.host").toLowerCase()
    private var port = env.getRequiredProperty("redirect.port").toInt()

    private val actualPort = if (isPortCanBeOmitted) "" else ":$port"

    private val isPortCanBeOmitted: Boolean
        get() = (proto == "http" && port == 80) || (proto == "https" && port == 443)

    fun makeUrlFromSelf(uri: String = ""): String {
        return "$proto://$host$actualPort/$uri"
    }

    fun formatUriForDatabase(uri: String) = uri.padStart(10, ' ')
}
