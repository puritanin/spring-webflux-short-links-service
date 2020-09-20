package ru.bicubictwice.springwebfluxshortlinksservice.service.generator

interface UriGenerator {
    var currentValue: String
    val nextValue: String
}
