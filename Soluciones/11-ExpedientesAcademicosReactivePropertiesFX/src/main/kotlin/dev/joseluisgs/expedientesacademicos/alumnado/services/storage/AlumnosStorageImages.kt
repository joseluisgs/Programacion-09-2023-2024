package dev.joseluisgs.expedientesacademicos.alumnado.services.storage

import com.github.michaelbull.result.Result
import dev.joseluisgs.expedientesacademicos.alumnado.errors.AlumnoError
import java.io.File

interface AlumnosStorageImages {
    fun saveImage(fileName: File): Result<File, AlumnoError>
    fun loadImage(fileName: String): Result<File, AlumnoError>
    fun deleteImage(fileName: File): Result<Unit, AlumnoError>
    fun deleteAllImages(): Result<Long, AlumnoError>
    fun updateImage(imagenName: String, newFileImage: File): Result<File, AlumnoError>
}