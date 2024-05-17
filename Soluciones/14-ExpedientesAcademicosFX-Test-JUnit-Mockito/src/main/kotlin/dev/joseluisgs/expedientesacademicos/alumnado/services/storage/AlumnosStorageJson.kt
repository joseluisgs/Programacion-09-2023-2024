package dev.joseluisgs.expedientesacademicos.alumnado.services.storage

import com.github.michaelbull.result.Result
import dev.joseluisgs.expedientesacademicos.alumnado.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno
import java.io.File

interface AlumnosStorageJson {
    fun storeDataJson(file: File, data: List<Alumno>): Result<Long, AlumnoError>
    fun loadDataJson(file: File): Result<List<Alumno>, AlumnoError>
}