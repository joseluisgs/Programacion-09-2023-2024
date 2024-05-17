package dev.joseluisgs.formulariofx.models

import java.time.LocalDate
import java.time.LocalDateTime

data class User(
    val nombre: String,
    val password: String,
    val email: String,
    val isRepetidor: Boolean = false,
    val curso: String = "1º DAW",
    val fechaNacimiento: LocalDate,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
