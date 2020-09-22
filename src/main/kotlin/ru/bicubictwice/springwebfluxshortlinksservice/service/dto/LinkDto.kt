package ru.bicubictwice.springwebfluxshortlinksservice.service.dto

import com.fasterxml.jackson.annotation.JsonProperty

class LinkDto {
    @JsonProperty(value = "short")
    var shortUrl: String? = null
}
