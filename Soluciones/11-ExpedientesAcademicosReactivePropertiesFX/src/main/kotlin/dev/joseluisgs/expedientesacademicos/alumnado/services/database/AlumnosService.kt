package dev.joseluisgs.expedientesacademicos.alumnado.services.database

import com.github.michaelbull.result.Result
import dev.joseluisgs.expedientesacademicos.alumnado.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno

interface AlumnosService {
    fun findAll(): Result<List<Alumno>, AlumnoError>
    fun deleteAll(): Result<Unit, AlumnoError>
    fun saveAll(alumnos: List<Alumno>): Result<List<Alumno>, AlumnoError>
    fun save(alumno: Alumno): Result<Alumno, AlumnoError>
    fun deleteById(id: Long): Result<Unit, AlumnoError>
    fun findById(id: Long): Result<Alumno, AlumnoError>
}