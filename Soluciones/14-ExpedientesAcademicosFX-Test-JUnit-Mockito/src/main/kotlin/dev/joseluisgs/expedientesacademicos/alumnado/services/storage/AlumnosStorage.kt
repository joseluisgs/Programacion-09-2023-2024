package dev.joseluisgs.expedientesacademicos.alumnado.services.storage

import com.github.michaelbull.result.Result
import dev.joseluisgs.expedientesacademicos.alumnado.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno
import java.io.File

// Implementamos un interfaz que hace de Fachada para el almacenamiento de los datos

interface AlumnosStorage {
    fun storeDataJson(file: File, data: List<Alumno>): Result<Long, AlumnoError>
    fun loadDataJson(file: File): Result<List<Alumno>, AlumnoError>
    fun saveImage(fileName: File): Result<File, AlumnoError>
    fun loadImage(fileName: String): Result<File, AlumnoError>
    fun deleteImage(fileName: File): Result<Unit, AlumnoError>
    fun deleteAllImages(): Result<Long, AlumnoError>
    fun updateImage(imagenName: String, newFileImage: File): Result<File, AlumnoError>
    fun exportToZip(fileToZip: File, data: List<Alumno>): Result<File, AlumnoError>
    fun loadFromZip(fileToUnzip: File): Result<List<Alumno>, AlumnoError>
}