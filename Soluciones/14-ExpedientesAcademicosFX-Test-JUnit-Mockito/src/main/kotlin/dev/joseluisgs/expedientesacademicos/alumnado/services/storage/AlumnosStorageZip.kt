package dev.joseluisgs.expedientesacademicos.alumnado.services.storage

import com.github.michaelbull.result.Result
import dev.joseluisgs.expedientesacademicos.alumnado.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno
import java.io.File

interface AlumnosStorageZip {
    fun exportToZip(fileToZip: File, data: List<Alumno>): Result<File, AlumnoError>
    fun loadFromZip(fileToUnzip: File): Result<List<Alumno>, AlumnoError>
}