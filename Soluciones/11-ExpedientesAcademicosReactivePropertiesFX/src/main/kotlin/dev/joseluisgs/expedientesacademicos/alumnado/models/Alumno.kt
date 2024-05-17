package dev.joseluisgs.expedientesacademicos.alumnado.models

import dev.joseluisgs.expedientesacademicos.locale.toLocalNumber
import java.time.LocalDate
import java.time.LocalDateTime

data class Alumno(
    val id: Long = NEW_ALUMNO,
    val apellidos: String,
    val nombre: String,
    val email: String,
    val fechaNacimiento: LocalDate,
    val calificacion: Double,
    val repetidor: Boolean,
    val imagen: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        const val NEW_ALUMNO = -1L
    }

    val nombreCompleto: String
        get() = "$apellidos, $nombre"

    val isNewAlumno: Boolean
        get() = id == NEW_ALUMNO

    val isAprobado: Boolean
        get() = calificacion >= 5.0

    val calificacionLocale: String
        get() = calificacion.toLocalNumber()
}