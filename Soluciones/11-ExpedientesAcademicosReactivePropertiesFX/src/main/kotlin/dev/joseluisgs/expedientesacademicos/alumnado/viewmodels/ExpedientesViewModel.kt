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
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.image.Image
import org.lighthousegames.logging.logging
import java.io.File
import java.time.LocalDate
import kotlin.properties.Delegates

private val logger = logging()

class ExpedientesViewModel(
    private val service: AlumnosService,
    private val storage: AlumnosStorage
) {

    // Estado del ViewModel
    val state = ExpedienteState()

    init {
        logger.debug { "Inicializando ExpedientesViewModel" }
        loadAllAlumnos() // Cargamos los datos del alumnado
        loadTypes() // Cargamos los tipos de repetidor
    }

    private fun loadTypes() {
        logger.debug { "Cargando tipos de repetidor" }
        state.typesRepetidor.clear()
        state.typesRepetidor.addAll(TipoFiltro.TODOS.value, TipoFiltro.SI.value, TipoFiltro.NO.value)
    }

    private fun loadAllAlumnos() {
        logger.debug { "Cargando alumnos del repositorio" }
        service.findAll().onSuccess {
            logger.debug { "Cargando alumnos del repositorio: ${state.alumnos.size}" }
            state.alumnos.clear()
            state.alumnos.addAll(it)
            updateActualState()
        }
    }

    // Actualiza el estado de la aplicación con los datos de ese instante en el estado
    private fun updateActualState() {
        logger.debug { "Actualizando estado de Aplicacion" }
        state.numAprobados.set(state.alumnos.count { it.isAprobado }.toString())
        val media = state.alumnos.map { it.calificacion }.average()
        state.notaMedia.set(if (media.isNaN()) "0.00" else media.round(2).toLocalNumber())
        state.alumnoSeleccionado.limpiar()
        state.alumnoOperacion.limpiar()
    }

    // Filtra la lista de alumnnos en el estado en función del tipo y el nombre completo
    fun alumnosFilteredList(tipo: String, nombreCompleto: String): FilteredList<Alumno> {
        logger.debug { "Filtrando lista de Alumnos: $tipo, $nombreCompleto" }

        return state.alumnos
            .filtered { alumno ->
                when (tipo) {
                    TipoFiltro.TODOS.value -> true
                    TipoFiltro.SI.value -> alumno.repetidor
                    TipoFiltro.NO.value -> !alumno.repetidor
                    else -> true
                }
            }.filtered { alumno ->
                alumno.nombreCompleto.contains(nombreCompleto, true)
            }

    }

    fun saveAlumnadoToJson(file: File): Result<Long, AlumnoError> {
        logger.debug { "Guardando Alumnado en JSON" }
        return storage.storeDataJson(file, state.alumnos)
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

        state.alumnoSeleccionado.apply {
            numero.value = alumno.id.toString()
            apellidos.value = alumno.apellidos
            nombre.value = alumno.nombre
            email.value = alumno.email
            fechaNacimiento.value = alumno.fechaNacimiento
            calificacion.value = alumno.calificacion.round(2).toLocalNumber()
            repetidor.value = alumno.repetidor
            storage.loadImage(alumno.imagen).onSuccess {
                imagen.value = Image(it.absoluteFile.toURI().toString())
                fileImage = it
            }.onFailure {
                imagen.value = Image(RoutesManager.getResourceAsStream("images/sin-imagen.png"))
                fileImage = File(RoutesManager.getResource("images/sin-imagen.png").toURI())
            }

        }
    }

    // Crea un nuevo alumno en el estado y repositorio
    fun crearAlumno(): Result<Alumno, AlumnoError> {
        logger.debug { "Creando Alumno" }
        // creamos el alumno
        val newAlumnoTemp = state.alumnoOperacion.copy()
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
                state.alumnos.add(it)
                updateActualState()
                Ok(it)
            }
        }
    }

    // Edita un alumno en el estado y repositorio
    fun editarAlumno(): Result<Alumno, AlumnoError> {
        logger.debug { "Editando Alumno" }
        // creamos el alumno
        val updatedAlumnoTemp = state.alumnoOperacion.copy()
        val fileNameTemp = state.alumnoSeleccionado.fileImage!!.name // Nombre de la imagen que tiene
        var updatedAlumno = state.alumnoOperacion.toModel().copy(imagen = fileNameTemp)
        return updatedAlumno.validate().andThen {
            // Tenemos dos opciones, que no tuviese imagen o que si la tuviese
            updatedAlumnoTemp.fileImage?.let { newFileImage ->
                if (updatedAlumno.imagen == TipoImagen.SIN_IMAGEN.value || updatedAlumno.imagen == TipoImagen.EMPTY.value) {
                    storage.saveImage(newFileImage).onSuccess {
                        updatedAlumno = updatedAlumno.copy(imagen = it.name)
                    }
                } else {
                    storage.updateImage(fileNameTemp, newFileImage)
                }
            }
            service.save(updatedAlumno).onSuccess {
                // El alumno ya está en la lista,saber su posición
                val index = state.alumnos.indexOfFirst { a -> a.id == it.id }
                UnidirectionalUnidirectional
                updateActualState()
                Ok(it)
            }
        }
    }

    // Elimina un alumno en el estado y repositorio
    fun eliminarAlumno(): Result<Unit, AlumnoError> {
        logger.debug { "Eliminando Alumno" }
        // Hay que eliminar su imagen
        val alumno = state.alumnoSeleccionado.copy()
        // Para evitar que cambien en la selección!!!
        val myId = alumno.numero.value.toLong()

        alumno.fileImage?.let {
            if (it.name != TipoImagen.SIN_IMAGEN.value) {
                storage.deleteImage(it)
            }
        }

        // Borramos del repositorio
        service.deleteById(myId)
        // Actualizamos la lista
        state.alumnos.removeIf { it.id == myId }
        updateActualState()
        return Ok(Unit)
    }

    // Actualiza la imagen del alumno en el estado
    fun updateImageAlumnoOperacion(fileImage: File) {
        logger.debug { "Actualizando imagen: $fileImage" }
        // Actualizamos la imagen
        state.alumnoOperacion.imagen.value = Image(fileImage.toURI().toString())
        // Actualizamos el fichero
        state.alumnoOperacion.fileImage = fileImage
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
        // Los contenedores de colecciones deben ser ObservableList
        val typesRepetidor: ObservableList<String> = FXCollections.observableArrayList<String>(),
        val alumnos: ObservableList<Alumno> = FXCollections.observableArrayList<Alumno>(),

        // Para las estadisticas
        val numAprobados: SimpleStringProperty = SimpleStringProperty("0"),
        val notaMedia: SimpleStringProperty = SimpleStringProperty("0.0"),

        // siempre que cambia el tipo de operacion, se actualiza el alumno
        val alumnoSeleccionado: AlumnoState = AlumnoState(), // Alumno seleccionado en tabla
        val alumnoOperacion: AlumnoState = AlumnoState(), // Alumno para operaciones (nuevo y editar)
    ) {
        // Y aquí un observable para la operacion
        // de esta forma, cuando cambie el tipo de operacion, se actualiza el alumno automaticamente
        var tipoOperacion: TipoOperacion by Delegates.observable(TipoOperacion.NUEVO) { _, _, newValue ->
            //println("Tipo de Operacion: $newValue")
            if (newValue == TipoOperacion.EDITAR) {
                logger.debug { "Copiando estado de Alumno Seleccionado a Operacion" }

                alumnoOperacion.numero.value = alumnoSeleccionado.numero.value
                alumnoOperacion.apellidos.value = alumnoSeleccionado.apellidos.value
                alumnoOperacion.nombre.value = alumnoSeleccionado.nombre.value
                alumnoOperacion.email.value = alumnoSeleccionado.email.value
                alumnoOperacion.fechaNacimiento.value = alumnoSeleccionado.fechaNacimiento.value
                alumnoOperacion.calificacion.value = alumnoSeleccionado.calificacion.value
                alumnoOperacion.repetidor.value = alumnoSeleccionado.repetidor.value
                alumnoOperacion.imagen.value = alumnoSeleccionado.imagen.value
            } else {
                logger.debug { "Limpiando estado de Alumno Operacion" }
                alumnoOperacion.limpiar()
            }
        }
    }

    // Estado para formularios de Alumno (seleccionado y de operaciones)
    data class AlumnoState(
        val numero: SimpleStringProperty = SimpleStringProperty(""),
        val apellidos: SimpleStringProperty = SimpleStringProperty(""),
        val nombre: SimpleStringProperty = SimpleStringProperty(""),
        val email: SimpleStringProperty = SimpleStringProperty(""),
        val fechaNacimiento: SimpleObjectProperty<LocalDate> = SimpleObjectProperty(LocalDate.now()),
        val calificacion: SimpleStringProperty = SimpleStringProperty(""),
        val repetidor: SimpleBooleanProperty = SimpleBooleanProperty(false),
        val imagen: SimpleObjectProperty<Image> = SimpleObjectProperty(Image(RoutesManager.getResourceAsStream("images/sin-imagen.png"))),
        var fileImage: File? = null
    ) {
        fun limpiar() {
            numero.value = ""
            apellidos.value = ""
            nombre.value = ""
            email.value = ""
            fechaNacimiento.value = LocalDate.now()
            calificacion.value = ""
            repetidor.value = false
            imagen.value = Image(RoutesManager.getResourceAsStream("images/sin-imagen.png"))
            fileImage = null
        }
    }


}

