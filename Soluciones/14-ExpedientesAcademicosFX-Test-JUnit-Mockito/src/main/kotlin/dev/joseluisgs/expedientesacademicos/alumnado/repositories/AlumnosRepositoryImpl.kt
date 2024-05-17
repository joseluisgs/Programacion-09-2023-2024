package dev.joseluisgs.expedientesacademicos.alumnado.repositories

import dev.joseluisgs.expedientesacademicos.alumnado.mappers.toModel
import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno
import dev.joseluisgs.expedientesacademicos.database.SqlDeLightClient
import org.lighthousegames.logging.logging
import java.time.LocalDateTime

private val logger = logging()

class AlumnosRepositoryImpl(
    private val databaseClient: SqlDeLightClient
) : AlumnosRepository {

    val db = databaseClient.dbQueries


    override fun findAll(): List<Alumno> {
        logger.debug { "findAll" }
        return db.selectAll().executeAsList().map { it.toModel() }
    }

    override fun findById(id: Long): Alumno? {
        logger.debug { "findById: $id" }
        return db.selectById(id).executeAsOneOrNull()?.toModel()
    }

    override fun save(alumno: Alumno): Alumno {
        logger.debug { "save: $alumno" }
        // Nunca se le cambia el ID, por lo que si es nuevo, lo creamos, si no lo actualizamos
        return if (alumno.isNewAlumno) {
            create(alumno)
        } else {
            update(alumno)
        }
    }

    private fun create(alumno: Alumno): Alumno {
        logger.debug { "create: $alumno" }
        val timeStamp = LocalDateTime.now().toString()
        // Insertamos y recuperamos el ID, transacción por funcion de sqlite (mira el .sq)
        db.transaction {
            db.insert(
                apellidos = alumno.apellidos,
                nombre = alumno.nombre,
                email = alumno.email,
                fechaNacimiento = alumno.fechaNacimiento.toString(),
                calificacion = alumno.calificacion,
                repetidor = if (alumno.repetidor) 1L else 0L,
                imagen = alumno.imagen,
                created_at = timeStamp,
                updated_at = timeStamp
            )
        }
        return db.selectLastInserted().executeAsOne().toModel()
    }

    private fun update(alumno: Alumno): Alumno {
        logger.debug { "update: $alumno" }
        val timeStamp = LocalDateTime.now().toString()
        db.update(
            id = alumno.id,
            apellidos = alumno.apellidos,
            nombre = alumno.nombre,
            email = alumno.email,
            fechaNacimiento = alumno.fechaNacimiento.toString(),
            calificacion = alumno.calificacion,
            repetidor = if (alumno.repetidor) 1L else 0L,
            imagen = alumno.imagen,
            updated_at = timeStamp
        )
        return alumno
    }

    override fun deleteById(id: Long) {
        logger.debug { "deleteById: $id" }
        return db.delete(id)
    }

    override fun deleteAll() {
        logger.debug { "deleteAll" }
        return db.deleteAll()
    }

    override fun saveAll(alumnos: List<Alumno>): List<Alumno> {
        logger.debug { "saveAll: $alumnos" }
        return alumnos.map { save(it) }
    }
}