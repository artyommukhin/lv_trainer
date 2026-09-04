package ru.artyommukhin.lvtrainer.dictionary.data

import java.util.Locale

class Language(
    val locale: Locale,
) {
    val symbol: String

    init {
        if (locale.country.isEmpty() || locale.country.isInt()) {
            symbol = locale.language
        } else {
            symbol = convertCountryTagToFlagEmoji(locale.country)
        }
    }

    override fun toString(): String {
        return "$symbol ${locale.displayLanguage} (${locale.toLanguageTag()})"
    }

    private fun String.isInt() = this.toIntOrNull() != null

    private fun convertCountryTagToFlagEmoji(tag: String): String = tag.map { char ->
        val shift = 127397
        val codePoint = char.code + shift
        String(Character.toChars(codePoint))
    }.joinToString("")
}


