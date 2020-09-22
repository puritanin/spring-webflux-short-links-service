package ru.bicubictwice.springwebfluxshortlinksservice.service.dto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.bicubictwice.springwebfluxshortlinksservice.data.model.Link
import ru.bicubictwice.springwebfluxshortlinksservice.service.Utils

@Component
class LinkDtoMapperImpl(
        @Autowired val utils: Utils
) : LinkDtoMapper {
    override fun toDto(link: Link): LinkDto = LinkDto().apply {
        this.shortUrl = utils.makeUrlFromSelf(link.shortUri!!.trim())
    }
}
