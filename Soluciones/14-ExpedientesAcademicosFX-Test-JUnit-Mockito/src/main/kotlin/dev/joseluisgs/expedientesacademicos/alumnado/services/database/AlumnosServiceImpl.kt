package dev.joseluisgs.expedientesacademicos.alumnado.services.database

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.joseluisgs.expedientesacademicos.alumnado.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno
import dev.joseluisgs.expedientesacademicos.alumnado.repositories.AlumnosRepository
import dev.joseluisgs.expedientesacademicos.alumnado.services.cache.AlumnosCache
import org.lighthousegames.logging.logging

private val logger = logging()

class AlumnosServiceImpl(
    private val repository: AlumnosRepository,
    private val cache: AlumnosCache
) : AlumnosService {
    override fun findAll(): Result<List<Alumno>, AlumnoError> {
        logger.debug { "findAll" }
        return Ok(repository.findAll())
    }

    override fun deleteAll(): Result<Unit, AlumnoError> {
        logger.debug { "deleteAll" }
        repository.deleteAll().also {
            cache.clear()
            return Ok(it)
        }
    }

    override fun saveAll(alumnos: List<Alumno>): Result<List<Alumno>, AlumnoError> {
        logger.debug { "saveAll" }
        repository.saveAll(alumnos).also {
            cache.clear()
            return Ok(it)
        }
    }

    override fun save(alumno: Alumno): Result<Alumno, AlumnoError> {
        logger.debug { "save" }
        repository.save(alumno).also {
            cache.put(alumno.id, alumno)
            return Ok(it)
        }
    }

    override fun deleteById(id: Long): Result<Unit, AlumnoError> {
        logger.debug { "deleteById" }
        repository.deleteById(id).also {
            cache.remove(id)
            return Ok(it)
        }
    }

    override fun findById(id: Long): Result<Alumno, AlumnoError> {
        logger.debug { "findById" }
        return cache.get(id)?.let {
            Ok(it)
        } ?: repository.findById(id)?.also {
            cache.put(id, it)
        }?.let {
            Ok(it)
        } ?: Err(AlumnoError.NotFound("Alumno con ID $id no encontrado"))
    }
}