package ru.bicubictwice.springwebfluxshortlinksservice.service.dto

import ru.bicubictwice.springwebfluxshortlinksservice.data.model.Link

interface LinkDtoMapper {
    fun toDto(link: Link): LinkDto
}
