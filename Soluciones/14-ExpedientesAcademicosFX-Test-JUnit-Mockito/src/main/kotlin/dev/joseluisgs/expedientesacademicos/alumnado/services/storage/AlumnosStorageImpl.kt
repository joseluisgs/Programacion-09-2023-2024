package dev.joseluisgs.expedientesacademicos.alumnado.services.storage

import com.github.michaelbull.result.Result
import dev.joseluisgs.expedientesacademicos.alumnado.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno
import dev.joseluisgs.expedientesacademicos.config.AppConfig
import org.lighthousegames.logging.logging
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

private val logger = logging()

class AlumnosStorageImpl(
    private val appConfig: AppConfig,
    private val storageJson: AlumnosStorageJson,
    private val storageZip: AlumnosStorageZip,
    private val storageImage: AlumnosStorageImages,
) : AlumnosStorage {

    init {
        // Creamos el directorio de imagenes si no existe
        logger.debug { "Creando directorio de imagenes si no existe" }
        Files.createDirectories(Paths.get(appConfig.imagesDirectory))
    }

    override fun storeDataJson(file: File, data: List<Alumno>): Result<Long, AlumnoError> {
        logger.debug { "Guardando datos en fichero $file" }
        return storageJson.storeDataJson(file, data)

    }

    override fun loadDataJson(file: File): Result<List<Alumno>, AlumnoError> {
        logger.debug { "Cargando datos en fichero $file" }
        return storageJson.loadDataJson(file)

    }


    override fun saveImage(fileName: File): Result<File, AlumnoError> {
        logger.debug { "Guardando imagen $fileName" }
        return storageImage.saveImage(fileName)
    }

    override fun loadImage(fileName: String): Result<File, AlumnoError> {
        logger.debug { "Cargando imagen $fileName" }
        return storageImage.loadImage(fileName)
    }

    override fun deleteImage(fileImage: File): Result<Unit, AlumnoError> {
        logger.debug { "Borrando imagen $fileImage" }
        return storageImage.deleteImage(fileImage)
    }

    override fun deleteAllImages(): Result<Long, AlumnoError> {
        logger.debug { "Borrando todas las imagenes" }
        return storageImage.deleteAllImages()
    }

    override fun updateImage(imagenName: String, newFileImage: File): Result<File, AlumnoError> {
        logger.debug { "Actualizando imagen $imagenName" }
        return storageImage.updateImage(imagenName, newFileImage)
    }

    override fun exportToZip(fileToZip: File, data: List<Alumno>): Result<File, AlumnoError> {
        logger.debug { "Exportando a ZIP $fileToZip" }
        return storageZip.exportToZip(fileToZip, data)
    }

    override fun loadFromZip(fileToUnzip: File): Result<List<Alumno>, AlumnoError> {
        logger.debug { "Importando desde ZIP $fileToUnzip" }
        return storageZip.loadFromZip(fileToUnzip)
    }
}

