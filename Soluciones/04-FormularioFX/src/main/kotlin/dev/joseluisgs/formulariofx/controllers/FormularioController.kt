package dev.joseluisgs.formulariofx.controllers

import dev.joseluisgs.formulariofx.locale.toDefaultDateString
import dev.joseluisgs.formulariofx.locale.toDefaultDateTimeString
import dev.joseluisgs.formulariofx.models.User
import javafx.fxml.FXML
import javafx.scene.control.*
import org.lighthousegames.logging.logging
import java.time.LocalDate
import java.util.*
import kotlin.system.exitProcess


private val logger = logging()

class FormularioController {

    // Importamos los elementos de la vista
    @FXML
    private lateinit var textNombre: TextField

    @FXML
    private lateinit var textPassword: TextField

    @FXML
    private lateinit var textEmail: TextField

    @FXML
    private lateinit var radioRepetidorSi: RadioButton

    @FXML
    private lateinit var comboCurso: ComboBox<String>

    @FXML
    private lateinit var dateNacimiento: DatePicker

    @FXML
    private lateinit var botonAceptar: Button

    @FXML
    private lateinit var botonCerrar: Button

    @FXML
    private lateinit var botonLimpiar: Button


    // Aquí inicializamos los valores por defecto si queremos
    @FXML
    fun initialize() {
        logger.debug { "Inicializando formulario" }
        // Inicializamos los valores por defecto
        initDefaultValues()
        // Inicializamos los eventos por defecto
        initDefaultEvents()

    }

    private fun initDefaultEvents() {
        botonAceptar.setOnAction { onActionAceptar() }
        botonCerrar.setOnAction { onActionCerrar() }
        botonLimpiar.setOnAction {
            logger.debug { "Limpiando formulario" }
            initDefaultValues()
        }
    }

    private fun initDefaultValues() {
        // Inicializamos los valores por defecto
        // Lo ponemos todo en español
        Locale.setDefault(Locale("es", "ES"))
        textNombre.text = ""
        textPassword.text = ""
        textEmail.text = ""
        dateNacimiento.value = LocalDate.now()
        radioRepetidorSi.isSelected = false
        comboCurso.items.addAll("1º DAW", "2º DAW", "1º DAM", "2º DAM")
    }

    private fun onActionCerrar() {
        logger.debug { "Cerrando formulario" }
        // Cerramos la aplicación
        Alert(Alert.AlertType.CONFIRMATION)
            .apply {
                title = "Confirmación"
                headerText = "¿Estás seguro de que quieres salir?"
                contentText = "Si sales perderás los datos introducidos"
            }.showAndWait()
            .filter { it == ButtonType.OK }
            .ifPresent { exitProcess(0) }
    }


    @FXML
    fun onActionAceptar() {
        logger.debug { "Procesando formulario" }
        if (!validateData()) {
            return
        }
        // Creamos el dato del usuario
        val user = User(
            nombre = textNombre.text, // Obtenemos el valor del campo
            password = textPassword.text, // Obtenemos el valor del campo
            email = textEmail.text, // Obtenemos el valor del campo
            isRepetidor = radioRepetidorSi.isSelected, // Obtenemos el valor del campo
            curso = comboCurso.selectionModel.selectedItem, // Obtenemos el valor del campo
            fechaNacimiento = dateNacimiento.value // Obtenemos el valor del campo

        )

        // Lo mostramos por consola
        println(user)

        // Mostramos la información
        showUserInformation(user)

    }

    private fun showUserInformation(user: User) {
        logger.debug { "Mostrando información del usuario" }
        Alert(Alert.AlertType.INFORMATION)
            .apply {
                title = "Información de usuario"
                headerText = "Datos del formulario de usuario registrado"
                contentText = "Nombre: ${user.nombre} \n" +
                        "Password: ${user.password} \n" +
                        "Email: ${user.email} \n" +
                        "Fecha de nacimiento: ${user.fechaNacimiento.toDefaultDateString()} \n" +
                        "Repetidor: ${if (user.isRepetidor) "Sí" else "No"} \n" +
                        "Curso: ${user.curso} \n" +
                        "Creado en: ${user.createdAt.toDefaultDateTimeString()}"
            }.showAndWait()

    }

    private fun validateData(): Boolean {
        logger.debug { "Validando datos" }
        if (textNombre.text.isBlank()) {
            showErrorAlert(
                title = "Error en formulario",
                header = "Error en nombre",
                content = "El nombre no puede estar vacío"
            )
            textNombre.requestFocus() // Ponemos el foco en el campo
            return false
        }
        if (textPassword.text.trim().isBlank()) {
            showErrorAlert(
                title = "Error en formulario",
                header = "Error en contraseña",
                content = "La contraseña no puede estar vacía"
            )
            textPassword.requestFocus() // Ponemos el foco en el campo
            return false
        }
        if (!textPassword.text.matches(Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}\$"))) {
            showErrorAlert(
                title = "Error en formulario",
                header = "Error en contraseña",
                content = "La contraseña debe tener al menos 8 caracteres y tener al menos un número y una letra mayúscula y minúscula"
            )
            textPassword.requestFocus() // Ponemos el foco en el campo
            return false
        }

        if (textEmail.text.isBlank() || !textEmail.text.matches(Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"))) {
            showErrorAlert(
                title = "Error en formulario",
                header = "Error en email",
                content = "El email no puede estar vacío y debe ser valido"
            )
            textEmail.requestFocus() // Ponemos el foco en el campo
            return false
        }

        if (dateNacimiento.value.isAfter(LocalDate.now())) {
            showErrorAlert(
                title = "Error en formulario",
                header = "Error en fecha de nacimiento",
                content = "La fecha de nacimiento no puede ser posterior a la actual: ${
                    LocalDate.now().toDefaultDateString()
                }"
            )
            dateNacimiento.requestFocus() // Ponemos el foco en el campo
            return false
        }
        if (comboCurso.selectionModel.isEmpty) {
            showErrorAlert(
                title = "Error en formulario",
                header = "Error en curso",
                content = "Debes seleccionar un curso"
            )
            comboCurso.requestFocus() // Ponemos el foco en el campo
            return false
        }
        return true
    }

    private fun showErrorAlert(title: String, header: String, content: String) {
        Alert(Alert.AlertType.ERROR)
            .apply {
                this.title = title
                headerText = header
                this.contentText = content
            }.showAndWait()
    }
}