package ru.bicubictwice.springwebfluxshortlinksservice.service.generator

import org.springframework.stereotype.Component

@Component
class SequentialUriGenerator : UriGenerator {
    private var array = Array(3) { alphabet[0] }

    override var currentValue: String
        @Synchronized
        get() = array.joinToString(separator = "")
        @Synchronized
        set(value) {
            if (value.isNotBlank() && value.all { alphabet.contains(it) }) {
                array = Array(value.length) { value[it] }
            }
        }

    override val nextValue: String
        @Synchronized
        get() {
            if (!incArray(array.size - 1)) {
                val value = currentValue
                currentValue = alphabet[0] + value
            }
            return currentValue
        }

    private fun incArray(pos: Int): Boolean {
        val nextIndex = alphabet.indexOf(array[pos]) + 1
        if (nextIndex == alphabet.size) {
            array[pos] = alphabet[0]
            return if (pos > 0) incArray(pos - 1) else false
        } else {
            array[pos] = alphabet[nextIndex]
        }
        return true
    }

    companion object {
        val alphabet = ('A'..'Z') + ('a'..'z')
    }
}
