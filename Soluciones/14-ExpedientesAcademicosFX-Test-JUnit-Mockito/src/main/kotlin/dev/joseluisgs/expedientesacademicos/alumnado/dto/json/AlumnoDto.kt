package dev.joseluisgs.expedientesacademicos.alumnado.dto.json

import kotlinx.serialization.Serializable

@Serializable
data class AlumnoDto(
    val id: Long,
    val apellidos: String,
    val nombre: String,
    val email: String,
    val fechaNacimiento: String,
    val calificacion: Double,
    val repetidor: Boolean,
    val imagen: String,
    val createdAt: String,
    val updatedAt: String
)