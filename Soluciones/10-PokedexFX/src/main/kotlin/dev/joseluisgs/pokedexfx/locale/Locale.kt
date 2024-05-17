package dev.joseluisgs.pokedexfx.locale

import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


private val locale = Locale.getDefault()
private val lang = locale.displayLanguage
private val country = locale.displayCountry
private val localeES = Locale.forLanguageTag("es-ES")

fun LocalDate.toLocalDate(): String {
    return this.format(
        DateTimeFormatter
            .ofLocalizedDate(FormatStyle.MEDIUM).withLocale(localeES)
    )
}

fun LocalDateTime.toLocalDateTime(): String {
    return this.format(
        DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(localeES)
    )
}

fun Double.toLocalMoney(): String {
    return NumberFormat.getCurrencyInstance(localeES).format(this)
}

fun Double.toLocalNumber(): String {
    return NumberFormat.getNumberInstance(localeES).format(this)
}