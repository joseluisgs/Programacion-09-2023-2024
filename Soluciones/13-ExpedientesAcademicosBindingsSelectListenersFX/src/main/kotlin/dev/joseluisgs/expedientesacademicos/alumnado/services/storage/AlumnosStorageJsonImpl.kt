package dev.joseluisgs.expedientesacademicos.alumnado.services.storage

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.joseluisgs.expedientesacademicos.alumnado.dto.json.AlumnoDto
import dev.joseluisgs.expedientesacademicos.alumnado.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.alumnado.mappers.toDto
import dev.joseluisgs.expedientesacademicos.alumnado.mappers.toModel
import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.lighthousegames.logging.logging
import java.io.File

private val logger = logging()

class AlumnosStorageJsonImpl : AlumnosStorageJson {
    override fun storeDataJson(file: File, data: List<Alumno>): Result<Long, AlumnoError> {
        logger.debug { "Guardando datos en fichero $file" }
        return try {
            val json = Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            }
            val jsonString = json.encodeToString<List<AlumnoDto>>(data.toDto())
            file.writeText(jsonString)
            Ok(data.size.toLong())
        } catch (e: Exception) {
            Err(AlumnoError.SaveJson("Error al escribir el JSON: ${e.message}"))
        }
    }

    override fun loadDataJson(file: File): Result<List<Alumno>, AlumnoError> {
        logger.debug { "Cargando datos en fichero $file" }
        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }
        // Debemos decirle el tipo de datos que queremos parsear
        return try {
            val jsonString = file.readText()
            val data = json.decodeFromString<List<AlumnoDto>>(jsonString)
            Ok(data.toModel())
        } catch (e: Exception) {
            Err(AlumnoError.LoadJson("Error al parsear el JSON: ${e.message}"))
        }
    }
}