package dev.joseluisgs.expedientesacademicos.alumnado.services.storage

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.joseluisgs.expedientesacademicos.alumnado.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.config.AppConfig
import org.lighthousegames.logging.logging
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.Instant

private val logger = logging()

class AlumnosStorageImagesImpl(
    private val appConfig: AppConfig
) : AlumnosStorageImages {

    private fun getImagenName(newFileImage: File): String {
        val name = newFileImage.name
        val extension = name.substring(name.lastIndexOf(".") + 1)
        return "${Instant.now().toEpochMilli()}.$extension"
    }

    override fun saveImage(fileName: File): Result<File, AlumnoError> {
        logger.debug { "Guardando imagen $fileName" }
        return try {
            val newFileImage = File(appConfig.imagesDirectory + getImagenName(fileName))
            Files.copy(fileName.toPath(), newFileImage.toPath(), StandardCopyOption.REPLACE_EXISTING)
            Ok(newFileImage)
        } catch (e: Exception) {
            Err(AlumnoError.SaveImage("Error al guardar la imagen: ${e.message}"))
        }
    }

    override fun loadImage(fileName: String): Result<File, AlumnoError> {
        logger.debug { "Cargando imagen $fileName" }
        val file = File(appConfig.imagesDirectory + fileName)
        return if (file.exists()) {
            Ok(file)
        } else {
            Err(AlumnoError.LoadImage("La imagen no existe: ${file.name}"))
        }
    }

    override fun deleteImage(fileName: File): Result<Unit, AlumnoError> {
        Files.deleteIfExists(fileName.toPath())
        return Ok(Unit)
    }

    override fun deleteAllImages(): Result<Long, AlumnoError> {
        logger.debug { "Borrando todas las imagenes" }
        return try {
            Ok(Files.walk(Paths.get(appConfig.imagesDirectory))
                .filter { Files.isRegularFile(it) }
                .map { Files.deleteIfExists(it) }
                .count())
        } catch (e: Exception) {
            Err(AlumnoError.DeleteImage("Error al borrar todas las imagenes: ${e.message}"))
        }
    }

    override fun updateImage(imagenName: String, newFileImage: File): Result<File, AlumnoError> {
        logger.debug { "Actualizando imagen $imagenName" }
        return try {
            val newUpdateImage = File(appConfig.imagesDirectory + imagenName)
            Files.copy(newFileImage.toPath(), newUpdateImage.toPath(), StandardCopyOption.REPLACE_EXISTING)
            Ok(newUpdateImage)
        } catch (e: Exception) {
            Err(AlumnoError.SaveImage("Error al guardar la imagen: ${e.message}"))
        }
    }
}