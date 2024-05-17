package dev.joseluisgs.expedientesacademicos.alumnado.controllers

import com.github.michaelbull.result.*
import dev.joseluisgs.expedientesacademicos.alumnado.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.alumnado.viewmodels.ExpedientesViewModel
import dev.joseluisgs.expedientesacademicos.alumnado.viewmodels.ExpedientesViewModel.TipoOperacion.EDITAR
import dev.joseluisgs.expedientesacademicos.alumnado.viewmodels.ExpedientesViewModel.TipoOperacion.NUEVO
import dev.joseluisgs.expedientesacademicos.routes.RoutesManager
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import java.time.LocalDate


private val logger = logging()

class DetalleViewController : KoinComponent {
    // Inyectamos nuestro ViewModel
    val viewModel: ExpedientesViewModel by inject()

    // Botones
    @FXML
    private lateinit var btnGuardar: Button

    @FXML
    private lateinit var btnLimpiar: Button

    @FXML
    private lateinit var btnCancelar: Button

    // Formulario del alumno
    @FXML
    private lateinit var textAlumnoNumero: TextField

    @FXML
    private lateinit var textAlumnoApellidos: TextField

    @FXML
    private lateinit var textAlumnoNombre: TextField

    @FXML
    private lateinit var textAlumnoEmail: TextField

    @FXML
    private lateinit var dateAlumnoFechaNacimiento: DatePicker

    @FXML
    private lateinit var textAlumnoCalificacion: TextField

    @FXML
    private lateinit var checkAlumnoRepetidor: CheckBox

    @FXML
    private lateinit var imageAlumno: ImageView

    @FXML
    fun initialize() {
        logger.debug { "Inicializando DetalleViewController FXML en Modo: ${viewModel.state.value.tipoOperacion}" }

        textAlumnoNumero.isEditable = false // No se puede editar el número

        // Iniciamos los valores
        initValues()

        // Iniciamos los bindings
        initBindings()

        // Iniciamos los eventos
        initEventos()
    }

    private fun initValues() {
        logger.debug { "initValues" }
        // Inicializamos los valores del formulario
        textAlumnoNumero.text = viewModel.state.value.alumno.numero
        textAlumnoApellidos.text = viewModel.state.value.alumno.apellidos
        textAlumnoNombre.text = viewModel.state.value.alumno.nombre
        textAlumnoEmail.text = viewModel.state.value.alumno.email
        dateAlumnoFechaNacimiento.value = viewModel.state.value.alumno.fechaNacimiento
        textAlumnoCalificacion.text = viewModel.state.value.alumno.calificacion
        checkAlumnoRepetidor.isSelected = viewModel.state.value.alumno.repetidor
        imageAlumno.image = viewModel.state.value.alumno.imagen
    }


    private fun initEventos() {
        // Botones
        btnGuardar.setOnAction {
            onGuardarAction()
        }
        btnLimpiar.setOnAction {
            onLimpiarAction()
        }
        btnCancelar.setOnAction {
            onCancelarAction()
        }

        imageAlumno.setOnMouseClicked {
            onImageAction()
        }
    }

    private fun onImageAction() {
        logger.debug { "onImageAction" }
        // Abrimos un diálogo para seleccionar una imagen, esta vez lo he hecho más compacto!!!
        // Comparalo con los de Json!!!
        FileChooser().run {
            title = "Selecciona una imagen"
            extensionFilters.addAll(FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"))
            showOpenDialog(RoutesManager.activeStage)
        }?.let {
            viewModel.updateImageAlumnoOperacion(it)
        }

    }

    private fun initBindings() {
        // Formulario, actuamos para actualizar solo la imagen porque los datos ya los hemos cargados!!
        viewModel.state.addListener { _, oldValue, newValue ->
            logger.debug { "Actualizando imagen del detalle" }
            // Vamos a poner if, porque si se actualiza la imagen, no queremos que se actualicen los campos
            if (oldValue.alumno.imagen != newValue.alumno.imagen) {
                imageAlumno.image = newValue.alumno.imagen
            }
        }
    }

    private fun onGuardarAction() {
        logger.debug { "onGuardarActio" }
        // Dependiendo del modo
        validateForm().andThen {
            // Actualizamos los datos del alumno operación del estado!! Como hemos hecho con la imagen!
            viewModel.updateDataAlumnoOperacion(
                apellidos = textAlumnoApellidos.text,
                nombre = textAlumnoNombre.text,
                email = textAlumnoEmail.text,
                fechaNacimiento = dateAlumnoFechaNacimiento.value,
                calificacion = textAlumnoCalificacion.text,
                isRepetidor = checkAlumnoRepetidor.isSelected,
                imageAlumno = imageAlumno.image,
            )

            when (viewModel.state.value.tipoOperacion) {
                NUEVO -> {
                    viewModel.crearAlumno()
                }

                EDITAR -> {
                    viewModel.editarAlumno()
                }
            }
        }.onSuccess {
            logger.debug { "Alumno salvado correctamente" }
            showAlertOperacion(
                AlertType.INFORMATION,
                "Alumno salvado",
                "Alumno salvado correctamente:\n${it.nombreCompleto}"
            )
            cerrarVentana()
        }.onFailure {
            logger.error { "Error al salvar alumno/a: ${it.message}" }
            showAlertOperacion(
                AlertType.ERROR,
                "Error al salvar alumno/a",
                "Se ha producido un error al salvar el alumno/a:\n${it.message}"
            )
        }

    }

    private fun cerrarVentana() {
        // truco coger el stage asociado a un componente
        btnCancelar.scene.window.hide()
    }

    private fun onCancelarAction() {
        logger.debug { "onCancelarAction" }
        //viewModel.limpiar()
        cerrarVentana()
    }

    private fun onLimpiarAction() {
        logger.debug { "onLimpiarAction" }
        // Limpiamos el estado actual
        //viewModel.limpiar()
        limpiarFormulario()
    }

    private fun limpiarFormulario() {
        logger.debug { "limpiarFormulario" }
        textAlumnoApellidos.clear()
        textAlumnoNombre.clear()
        textAlumnoEmail.clear()
        dateAlumnoFechaNacimiento.value = null
        textAlumnoCalificacion.clear()
        checkAlumnoRepetidor.isSelected = false
        imageAlumno.image = Image(RoutesManager.getResourceAsStream("images/sin-imagen.png"))
    }

    // Lo puedo hacer aquí o en mi validador en el viewModel
    private fun validateForm(): Result<Unit, AlumnoError> {
        logger.debug { "validateForm" }

        // Validacion del formulario
        if (textAlumnoApellidos.text.isNullOrEmpty()) {
            return Err(AlumnoError.ValidationProblem("Apellidos no puede estar vacío"))
        }
        if (textAlumnoNombre.text.isNullOrEmpty()) {
            return Err(AlumnoError.ValidationProblem("Nombre no puede estar vacío"))
        }
        // Validamos el email, expresión regular
        if (textAlumnoEmail.text.isNullOrEmpty() || !textAlumnoEmail.text.matches(Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+"))) {
            return Err(AlumnoError.ValidationProblem("Email no puede estar vacío o no tiene el formato correcto"))
        }
        if (dateAlumnoFechaNacimiento.value == null || dateAlumnoFechaNacimiento.value.isAfter(LocalDate.now())) {
            return Err(AlumnoError.ValidationProblem("Fecha de nacimiento no puede estar vacía y debe ser anterior a hoy"))
        }
        if (textAlumnoCalificacion.text.isNullOrEmpty() || textAlumnoCalificacion.text.replace(",", ".")
                .toDoubleOrNull() == null || textAlumnoCalificacion.text.replace(",", ".")
                .toDouble() < 0 || textAlumnoCalificacion.text.replace(",", ".").toDouble() > 10
        ) {
            return Err(AlumnoError.ValidationProblem("Calificación no puede estar vacía y debe ser un número entre 0 y 10"))
        }
        return Ok(Unit)
    }

    private fun showAlertOperacion(
        alerta: AlertType = AlertType.CONFIRMATION,
        title: String = "",
        mensaje: String = ""
    ) {
        val alert = Alert(alerta)
        alert.title = title
        alert.contentText = mensaje
        alert.showAndWait()
    }


}



