package dev.joseluisgs.formulariofx.locale

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun LocalDateTime.toDefaultDateTimeString(): String {
    return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(this)
}

fun LocalDate.toDefaultDateString(): String {
    return DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()).format(this)
}
