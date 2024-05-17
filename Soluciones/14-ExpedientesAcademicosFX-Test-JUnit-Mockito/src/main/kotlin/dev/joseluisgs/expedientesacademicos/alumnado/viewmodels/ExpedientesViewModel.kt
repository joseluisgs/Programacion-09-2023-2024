package dev.joseluisgs.expedientesacademicos.alumnado.viewmodels

import com.github.michaelbull.result.*
import dev.joseluisgs.expedientesacademicos.alumnado.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.alumnado.mappers.toModel
import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno
import dev.joseluisgs.expedientesacademicos.alumnado.services.database.AlumnosService
import dev.joseluisgs.expedientesacademicos.alumnado.services.storage.AlumnosStorage
import dev.joseluisgs.expedientesacademicos.alumnado.validators.validate
import dev.joseluisgs.expedientesacademicos.locale.round
import dev.joseluisgs.expedientesacademicos.locale.toLocalNumber
import dev.joseluisgs.expedientesacademicos.routes.RoutesManager
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import org.lighthousegames.logging.logging
import java.io.File
import java.time.LocalDate


private val logger = logging()

class ExpedientesViewModel(
    private val service: AlumnosService,
    private val storage: AlumnosStorage
) {

    // Estado del ViewModel
    val state: SimpleObjectProperty<ExpedienteState> = SimpleObjectProperty(ExpedienteState())

    init {
        logger.debug { "Inicializando ExpedientesViewModel" }
        loadAllAlumnos() // Cargamos los datos del alumnado
        loadTypes() // Cargamos los tipos de repetidor
    }

    private fun loadTypes() {
        logger.debug { "Cargando tipos de repetidor" }
        state.value = state.value.copy(typesRepetidor = TipoFiltro.entries.map { it.value })
    }

    private fun loadAllAlumnos() {
        logger.debug { "Cargando alumnos del repositorio" }
        service.findAll().onSuccess {
            logger.debug { "Cargando alumnos del repositorio: ${it.size}" }
            state.value = state.value.copy(alumnos = it)
            updateActualState()
        }
    }

    // Actualiza el estado de la aplicación con los datos de ese instante en el estado
    private fun updateActualState() {
        logger.debug { "Actualizando estado de Aplicacion" }
        val numnumAprobados = state.value.alumnos.count { it.isAprobado }.toString()
        val media = state.value.alumnos.map { it.calificacion }.average()
        val notaMedia = if (media.isNaN()) "0.00" else media.round(2).toLocalNumber()
        // Solo toca el estado una vez para evitar problemas de concurrencia
        state.value = state.value.copy(
            numAprobados = numnumAprobados,
            notaMedia = notaMedia,
            alumno = AlumnoState()
        )
    }

    // Filtra la lista de alumnnos en el estado en función del tipo y el nombre completo
    fun alumnosFilteredList(tipo: String, nombreCompleto: String): List<Alumno> {
        logger.debug { "Filtrando lista de Alumnos: $tipo, $nombreCompleto" }

        return state.value.alumnos
            .filter { alumno ->
                when (tipo) {
                    TipoFiltro.TODOS.value -> true
                    TipoFiltro.SI.value -> alumno.repetidor
                    TipoFiltro.NO.value -> !alumno.repetidor
                    else -> true
                }
            }.filter { alumno ->
                alumno.nombreCompleto.contains(nombreCompleto, true)
            }

    }

    fun saveAlumnadoToJson(file: File): Result<Long, AlumnoError> {
        logger.debug { "Guardando Alumnado en JSON" }
        return storage.storeDataJson(file, state.value.alumnos)
    }

    fun loadAlumnadoFromJson(file: File, withImages: Boolean = false): Result<List<Alumno>, AlumnoError> {
        logger.debug { "Cargando Alumnado en JSON" }
        // Borramos todas las imagenes e iniciamos el proceso
        return storage.deleteAllImages().andThen {
            storage.loadDataJson(file).onSuccess {
                service.deleteAll() // Borramos todos los datos de la BD
                // Guardamos los nuevos, pero hay que quitarle el ID, porque trabajamos con el NEW!!
                service.saveAll(
                    if (withImages)
                        it
                    else
                        it.map { a -> a.copy(id = Alumno.NEW_ALUMNO, imagen = TipoImagen.SIN_IMAGEN.value) }
                )
                loadAllAlumnos() // Actualizamos la lista
            }
        }
    }

    // carga en el estado el alumno seleccionado
    fun updateAlumnoSeleccionado(alumno: Alumno) {
        logger.debug { "Actualizando estado de Alumno: $alumno" }

        // Datos de la imagen
        var imagen = Image(RoutesManager.getResourceAsStream("images/sin-imagen.png"))
        var fileImage = File(RoutesManager.getResource("images/sin-imagen.png").toURI())

        storage.loadImage(alumno.imagen).onSuccess {
            imagen = Image(it.absoluteFile.toURI().toString())
            fileImage = it
        }

        state.value = state.value.copy(
            alumno = AlumnoState(
                numero = alumno.id.toString(),
                apellidos = alumno.apellidos,
                nombre = alumno.nombre,
                email = alumno.email,
                fechaNacimiento = alumno.fechaNacimiento,
                calificacion = alumno.calificacion.round(2).toLocalNumber(),
                repetidor = alumno.repetidor,
                imagen = imagen,
                fileImage = fileImage
            )
        )

    }

    // Crea un nuevo alumno en el estado y repositorio
    fun crearAlumno(): Result<Alumno, AlumnoError> {
        logger.debug { "Creando Alumno" }
        // creamos el alumno
        val newAlumnoTemp = state.value.alumno.copy()
        var newAlumno = newAlumnoTemp.toModel().copy(id = Alumno.NEW_ALUMNO)
        return newAlumno.validate().andThen {
            // Copiamos la imagen si no es nula
            newAlumnoTemp.fileImage?.let { newFileImage ->
                storage.saveImage(newFileImage).onSuccess {
                    newAlumno = newAlumno.copy(imagen = it.name)
                }
            }
            // Guardamos el alumno en el repositorio
            service.save(newAlumno).andThen {
                state.value = state.value.copy(
                    alumnos = state.value.alumnos + it
                )
                updateActualState()
                Ok(it)
            }
        }
    }

    // Edita un alumno en el estado y repositorio
    fun editarAlumno(): Result<Alumno, AlumnoError> {
        logger.debug { "Editando Alumno" }
        // actualizamos el alumno, atención a la imagen
        //println("La imagen es: ${state.value.alumno.imagen}")
        //println("La nueva es es: ${state.value.alumno.fileImage}")
        // println("La imagen antigua es: ${state.value.alumno.oldFileImage}")

        val updatedAlumnoTemp = state.value.alumno.copy() // Copiamos el estado
        val fileNameTemp = state.value.alumno.oldFileImage?.name
            ?: TipoImagen.SIN_IMAGEN.value // Si no hay imagen, ponemos la sin imagen
        var updatedAlumno = state.value.alumno.toModel().copy(imagen = fileNameTemp) // Copiamos el estado a modelo
        return updatedAlumno.validate().andThen {
            // Tenemos dos opciones, que no tuviese imagen o que si la tuviese
            updatedAlumnoTemp.fileImage?.let { newFileImage ->
                if (updatedAlumno.imagen == TipoImagen.SIN_IMAGEN.value || updatedAlumno.imagen == TipoImagen.EMPTY.value) {
                    // Si no tiene imagen, la guardamos
                    storage.saveImage(newFileImage).onSuccess {
                        updatedAlumno = updatedAlumno.copy(imagen = it.name) // Actualizamos la imagen
                    }
                } else {
                    // Si tiene imagen, la actualizamos con la nueva, pero hay que borrar la antigua, por si se cambia
                    storage.updateImage(fileNameTemp, newFileImage)
                }
            }
            service.save(updatedAlumno).onSuccess {
                // El alumno ya está en la lista,saber su posición
                val index = state.value.alumnos.indexOfFirst { a -> a.id == it.id }
                state.value = state.value.copy(
                    alumnos = state.value.alumnos.toMutableList().apply { this[index] = it }
                )
                updateActualState()
                Ok(it)
            }
        }
    }

    // Elimina un alumno en el estado y repositorio
    fun eliminarAlumno(): Result<Unit, AlumnoError> {
        logger.debug { "Eliminando Alumno" }
        // Hay que eliminar su imagen
        val alumno = state.value.alumno.copy()
        // Para evitar que cambien en la selección!!!
        val myId = alumno.numero.toLong()

        alumno.fileImage?.let {
            if (it.name != TipoImagen.SIN_IMAGEN.value) {
                storage.deleteImage(it)
            }
        }

        // Borramos del repositorio
        service.deleteById(myId)
        // Actualizamos la lista
        state.value = state.value.copy(
            alumnos = state.value.alumnos.toMutableList().apply { this.removeIf { it.id == myId } }
        )
        updateActualState()
        return Ok(Unit)
    }

    // Actualiza la imagen del alumno en el estado
    fun updateImageAlumnoOperacion(fileImage: File) {
        logger.debug { "Actualizando imagen: $fileImage" }
        // Actualizamos la imagen
        state.value = state.value.copy(
            alumno = state.value.alumno.copy(
                imagen = Image(fileImage.toURI().toString()),
                fileImage = fileImage,
                oldFileImage = state.value.alumno.fileImage // Guardamos la antigua por si hay que cambiar al editar y actualizar la imagen
            )
        )
    }

    fun exportToZip(fileToZip: File): Result<Unit, AlumnoError> {
        logger.debug { "Exportando a ZIP: $fileToZip" }
        // recogemos los alumnos del repositorio
        service.findAll().andThen {
            storage.exportToZip(fileToZip, it)
        }.onFailure {
            logger.error { "Error al exportar a ZIP: ${it.message}" }
            return Err(it)
        }
        return Ok(Unit)
    }

    fun loadAlumnadoFromZip(fileToUnzip: File): Result<List<Alumno>, AlumnoError> {
        logger.debug { "Importando de ZIP: $fileToUnzip" }
        // recogemos los alumnos del repositorio
        return storage.loadFromZip(fileToUnzip).onSuccess { lista ->
            service.deleteAll().andThen {
                service.saveAll(lista.map { a -> a.copy(id = Alumno.NEW_ALUMNO) })
            }.onSuccess {
                loadAllAlumnos()
            }
        }
    }

    fun changeAlumnoOperacion(newValue: TipoOperacion) {
        logger.debug { "Cambiando tipo de operacion: $newValue" }
        if (newValue == TipoOperacion.EDITAR) {
            logger.debug { "Copiando estado de Alumno Seleccionado a Operacion" }
            state.value = state.value.copy(
                alumno = state.value.alumno.copy(),
                tipoOperacion = newValue
            )

        } else {
            logger.debug { "Limpiando estado de Alumno Operacion" }
            state.value = state.value.copy(
                alumno = AlumnoState(),
                tipoOperacion = newValue
            )
        }
    }


    fun updateDataAlumnoOperacion(
        apellidos: String,
        nombre: String,
        email: String,
        fechaNacimiento: LocalDate,
        calificacion: String,
        isRepetidor: Boolean,
        imageAlumno: Image
    ) {
        logger.debug { "Actualizando estado de Alumno Operacion" }
        state.value = state.value.copy(
            alumno = state.value.alumno.copy(
                apellidos = apellidos,
                nombre = nombre,
                email = email,
                fechaNacimiento = fechaNacimiento,
                calificacion = calificacion,
                repetidor = isRepetidor,
                imagen = imageAlumno,
                //fileImage = fileImage // No se actualiza aquí, se actualiza en el método de la imagen
            )
        )

    }

    // Mi estado
    // Enums
    enum class TipoFiltro(val value: String) {
        TODOS("Todos/as"), SI("Sí"), NO("No")
    }

    enum class TipoOperacion(val value: String) {
        NUEVO("Nuevo"), EDITAR("Editar")
    }

    enum class TipoImagen(val value: String) {
        SIN_IMAGEN("sin-imagen.png"), EMPTY("")
    }

    // Clases que representan el estado
    // Estado del ViewModel y caso de uso de Gestión de Expedientes
    data class ExpedienteState(
        // Los contenedores de colecciones
        val typesRepetidor: List<String> = emptyList(),
        val alumnos: List<Alumno> = emptyList(),

        // Para las estadisticas
        val numAprobados: String = "0",
        val notaMedia: String = "0.0",

        // siempre que cambia el tipo de operacion, se actualiza el alumno
        val alumno: AlumnoState = AlumnoState(), // Estado del alumno seleccionado

        val tipoOperacion: TipoOperacion = TipoOperacion.NUEVO
    )

    // Estado para formularios de Alumno (seleccionado y de operaciones)
    data class AlumnoState(
        val numero: String = "",
        val apellidos: String = "",
        val nombre: String = "",
        val email: String = "",
        val fechaNacimiento: LocalDate = LocalDate.now(),
        val calificacion: String = "",
        val repetidor: Boolean = false,
        val imagen: Image = Image(RoutesManager.getResourceAsStream("images/sin-imagen.png")),
        val fileImage: File? = null,
        val oldFileImage: File? = null // Para controlar si se ha cambiado la imagen y borrarla
    )

}

