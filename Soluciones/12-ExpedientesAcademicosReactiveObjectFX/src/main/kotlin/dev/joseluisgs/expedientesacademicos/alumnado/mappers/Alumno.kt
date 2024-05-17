package dev.joseluisgs.expedientesacademicos.alumnado.mappers

import database.AlumnoEntity
import dev.joseluisgs.expedientesacademicos.alumnado.dto.json.AlumnoDto
import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno
import dev.joseluisgs.expedientesacademicos.alumnado.viewmodels.ExpedientesViewModel.AlumnoState
import java.time.LocalDate
import java.time.LocalDateTime

fun AlumnoDto.toModel(): Alumno {
    return Alumno(
        id,
        apellidos,
        nombre,
        email,
        LocalDate.parse(fechaNacimiento),
        calificacion,
        repetidor,
        imagen,
        LocalDateTime.parse(createdAt),
        LocalDateTime.parse(updatedAt)
    )
}

fun List<AlumnoDto>.toModel(): List<Alumno> {
    return map { it.toModel() }
}

fun Alumno.toDto(): AlumnoDto {
    return AlumnoDto(
        id,
        apellidos,
        nombre,
        email,
        fechaNacimiento.toString(),
        calificacion,
        repetidor,
        imagen,
        createdAt.toString(),
        updatedAt.toString()
    )
}

fun List<Alumno>.toDto(): List<AlumnoDto> {
    return map { it.toDto() }
}

fun AlumnoEntity.toModel(): Alumno {
    return Alumno(
        id,
        apellidos,
        nombre,
        email,
        LocalDate.parse(fechaNacimiento),
        calificacion,
        repetidor == 1L,
        imagen,
        LocalDateTime.parse(created_at),
        LocalDateTime.parse(updated_at)
    )
}

fun AlumnoState.toModel(): Alumno {
    return Alumno(
        id = if (numero.trim().isBlank()) Alumno.NEW_ALUMNO else numero.toLong(),
        apellidos = apellidos.trim(),
        nombre = nombre.trim(),
        email = email.trim(),
        fechaNacimiento = fechaNacimiento,
        calificacion = calificacion.trim().replace(",", ".").toDouble(),
        repetidor = repetidor,
        imagen = imagen.url ?: "sin-imagen.png",
    )
}

