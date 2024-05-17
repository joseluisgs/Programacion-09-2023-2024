package dev.joseluisgs.expedientesacademicos.alumnado.controllers

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno
import dev.joseluisgs.expedientesacademicos.alumnado.viewmodels.ExpedientesViewModel
import dev.joseluisgs.expedientesacademicos.alumnado.viewmodels.ExpedientesViewModel.TipoOperacion
import dev.joseluisgs.expedientesacademicos.routes.RoutesManager
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.Cursor.DEFAULT
import javafx.scene.Cursor.WAIT
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging


private val logger = logging()

class ExpedientesViewConroller : KoinComponent {
    // Inyectamos nuestro ViewModel
    val viewModel: ExpedientesViewModel by inject()

    // Menus
    @FXML
    private lateinit var menuImportar: MenuItem

    @FXML
    private lateinit var menuExportar: MenuItem

    @FXML
    private lateinit var menuZip: MenuItem

    @FXML
    private lateinit var menuUnzip: MenuItem

    @FXML
    private lateinit var menuSalir: MenuItem

    @FXML
    private lateinit var menuAcercaDe: MenuItem

    // Botones
    @FXML
    private lateinit var btnNuevo: Button

    @FXML
    private lateinit var btnEditar: Button

    @FXML
    private lateinit var btnEliminar: Button

    //Combo
    @FXML
    private lateinit var comboTipo: ComboBox<String>

    // Tabla
    @FXML
    private lateinit var tableAlumnos: TableView<Alumno>

    @FXML
    private lateinit var tableColumnNumero: TableColumn<Alumno, Long>

    @FXML
    private lateinit var tableColumNombre: TableColumn<Alumno, String>

    @FXML
    private lateinit var tableColumnCalificacion: TableColumn<Alumno, Double>

    // Buscador
    @FXML
    private lateinit var textBuscador: TextField

    // Estadisticas
    @FXML
    private lateinit var textNumAprobados: TextField

    @FXML
    private lateinit var textNotaMedia: TextField

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

    // Metodo para inicializar
    @FXML
    fun initialize() {
        logger.debug { "Inicializando ExpedientesDeViewController FXML" }

        initDefaultValues()

        // Iniciamos los default data y bindings
        initBindings()

        // Iniciamos los eventos
        initEventos()
    }

    private fun initDefaultValues() {
        logger.debug { "Inicializando valores por defecto" }

        // comboBox
        comboTipo.items = FXCollections.observableArrayList(viewModel.state.value.typesRepetidor)
        comboTipo.selectionModel.selectFirst()

        // Tabla
        tableAlumnos.items = FXCollections.observableArrayList(viewModel.state.value.alumnos)

        // columnas, con el nombre de la propiedad del objeto hará binding
        tableColumnNumero.cellValueFactory = PropertyValueFactory("id")
        tableColumNombre.cellValueFactory = PropertyValueFactory("nombreCompleto")
        tableColumnCalificacion.cellValueFactory = PropertyValueFactory("calificacionLocale")
    }


    private fun initBindings() {
        logger.debug { "Inicializando bindings" }

        /*
        // Asociamos el observer del estado

           viewModel.state.addListener { _, _, newValue ->
               logger.debug { "Actualizando datos de la vista" }
               // Actualizamos la tabla
               // Vamos a poner un if para evitar que se lance el evento de selección
               if (tableAlumnos.items != newValue.alumnos) {
                   tableAlumnos.items = FXCollections.observableArrayList(newValue.alumnos)
               }
               // Estadisticas
               textNumAprobados.text = newValue.numAprobados
               textNotaMedia.text = newValue.notaMedia

               // si queremos condiciones haríamos
               if (newValue.numAprobados.toInt() > 1000) {
                   textNumAprobados.text = "Son muchos aprobados"
               } else {
                     textNumAprobados.text = newValue.numAprobados
               }

               // Formulario
               textAlumnoNumero.text = newValue.alumno.numero
               textAlumnoApellidos.text = newValue.alumno.apellidos
               textAlumnoNombre.text = newValue.alumno.nombre
               textAlumnoEmail.text = newValue.alumno.email
               dateAlumnoFechaNacimiento.value = newValue.alumno.fechaNacimiento
               textAlumnoCalificacion.text = newValue.alumno.calificacion
               checkAlumnoRepetidor.isSelected = newValue.alumno.repetidor
               imageAlumno.image = newValue.alumno.imagen

           }*/

        // ahora vamos a hacer las propiedades de solo lectura directamente
        // De esta manera solo se actualiza cuando cambia el valor y cada propiedad se actualiza por separado
        // No cambia todo como pasaba antes que si cambiaba una se actualizaban todas

        textNumAprobados.textProperty().bind(viewModel.state.map { it.numAprobados })
        // también se puede hacer con Bindings.select
        // textNotaMedia.textProperty().bind(viewModel.state.map { it.notaMedia })
        textNotaMedia.textProperty().bind(Bindings.select(viewModel.state, "notaMedia"))


        // Si queremos condiciones
        /*textNumAprobados.textProperty().bind(viewModel.state.map {
            if (it.numAprobados.toInt() > 1000) {
                "Son muchos aprobados"
            } else {
                it.numAprobados
            }
        })*/

        // Formulario
        textAlumnoNumero.textProperty().bind(viewModel.state.map { it.alumno.numero })
        textAlumnoApellidos.textProperty().bind(viewModel.state.map { it.alumno.apellidos })
        textAlumnoNombre.textProperty().bind(viewModel.state.map { it.alumno.nombre })
        textAlumnoEmail.textProperty().bind(viewModel.state.map { it.alumno.email })
        dateAlumnoFechaNacimiento.valueProperty().bind(viewModel.state.map { it.alumno.fechaNacimiento })
        textAlumnoCalificacion.textProperty().bind(viewModel.state.map { it.alumno.calificacion })
        checkAlumnoRepetidor.selectedProperty().bind(viewModel.state.map { it.alumno.repetidor })
        imageAlumno.imageProperty().bind(viewModel.state.map { it.alumno.imagen })

        // Pero que pasa con la tabla?
        // Si lo haces con FXCollections.observableArrayList(viewModel.state.value.alumnos) no se actualiza
        // Porque no se actualiza la lista, se actualiza el contenido de la lista
        // Por eso se hace con el listener
        viewModel.state.addListener { _, _, newValue ->
            logger.debug { "Actualizando datos de la vista" }
            // Actualizamos la tabla y solo si ha cambiado
            // Vamos a poner un if para evitar que se lance el evento de selección
            if (tableAlumnos.items != newValue.alumnos) {
                tableAlumnos.items = FXCollections.observableArrayList(newValue.alumnos)
            }
        }

        // Para que no se vea desactivado mucho, que queda feo!!
        dateAlumnoFechaNacimiento.style = "-fx-opacity: 1"
        dateAlumnoFechaNacimiento.editor.style = "-fx-opacity: 1"
        checkAlumnoRepetidor.style = "-fx-opacity: 1"
    }

    private fun initEventos() {
        logger.debug { "Inicializando eventos" }

        // menús
        menuImportar.setOnAction { onImportarAction() }

        menuExportar.setOnAction { onExportarAction() }

        menuZip.setOnAction { onZipAction() }

        menuUnzip.setOnAction { onUnzipAction() }

        menuSalir.setOnAction { RoutesManager.onAppExit() }

        menuAcercaDe.setOnAction { onAcercaDeAction() }

        // Botones
        btnNuevo.setOnAction { onNuevoAction() }

        btnEditar.setOnAction { onEditarAction() }

        btnEliminar.setOnAction { onEliminarAction() }

        // Combo
        comboTipo.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            newValue?.let { onComboSelected(newValue) }
        }

        // Tabla
        tableAlumnos.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            newValue?.let { onTablaSelected(newValue) }
        }

        // Buscador
        // Evento del buscador key press
        // Funciona con el intro
        // textBuscador.setOnAction {
        // con cualquer letra al levantarse, ya ha cambiado el valor
        textBuscador.setOnKeyReleased { newValue ->
            newValue?.let { onKeyReleasedAction() }
        }
    }

    private fun onKeyReleasedAction() {
        logger.debug { "onKeyReleasedAction" }
        filterDataTable()
    }

    private fun filterDataTable() {
        logger.debug { "filterDataTable" }
        // filtramos por el tipo seleccionado en la tabla
        tableAlumnos.items =
            FXCollections.observableList(viewModel.alumnosFilteredList(comboTipo.value, textBuscador.text.trim()))
    }

    private fun onTablaSelected(newValue: Alumno) {
        logger.debug { "onTablaSelected: $newValue" }
        viewModel.updateAlumnoSeleccionado(newValue)
    }

    private fun onComboSelected(newValue: String) {
        logger.debug { "onComboSelected: $newValue" }
        filterDataTable()
    }

    private fun onEliminarAction() {
        logger.debug { "onEliminarAction" }
        // Comprbar que se ha seleccionado antes!!
        if (tableAlumnos.selectionModel.selectedItem == null) {
            return
        }
        Alert(AlertType.CONFIRMATION).apply {
            title = "¿Eliminar Alumno/?"
            contentText =
                "¿Desea eliminar este alumno?\nEsta acción no se puede deshacer y se eliminarán todos los datos asociados al alumno."
        }.showAndWait().ifPresent {
            if (it == ButtonType.OK) {
                viewModel.eliminarAlumno().onSuccess {
                    logger.debug { "Alumno/a eliminado correctamente" }
                    showAlertOperacion(
                        alerta = AlertType.INFORMATION,
                        "Alumno/a eliminado/a",
                        "Se ha eliminado el/la alumno/a correctamente del sistema"
                    )
                    tableAlumnos.selectionModel.clearSelection()
                }.onFailure {
                    logger.error { "Error al eliminar el alumno: ${it.message}" }
                    showAlertOperacion(alerta = AlertType.ERROR, "Error al eliminar el/la alumno/a", it.message)
                }
            }
        }

    }

    private fun onEditarAction() {
        logger.debug { "onEditarAction" }
        if (tableAlumnos.selectionModel.selectedItem == null) {
            return
        }
        viewModel.changeAlumnoOperacion(TipoOperacion.EDITAR)
        RoutesManager.initDetalle()

    }

    private fun onNuevoAction() {
        logger.debug { "onNuevoAction" }
        // Poner el modo nuevo antes!!
        viewModel.changeAlumnoOperacion(TipoOperacion.NUEVO)
        RoutesManager.initDetalle()
    }

    private fun onAcercaDeAction() {
        logger.debug { "onAcercaDeAction" }
        RoutesManager.initAcercaDeStage()
    }

    private fun onExportarAction() {
        logger.debug { "onExportarAction" }
        // Forma larga, muy Java
        //val fileChooser = FileChooser()
        //fileChooser.title = "Exportar expedientes"
        //fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("JSON", "*.json"))
        //val file = fileChooser.showSaveDialog(RoutesManager.activeStage)

        // Forma Kotlin con run y let (scope functions)
        FileChooser().run {
            title = "Exportar expedientes"
            extensionFilters.add(FileChooser.ExtensionFilter("JSON", "*.json"))
            showSaveDialog(RoutesManager.activeStage)
        }?.let {
            logger.debug { "onSaveAction: $it" }
            RoutesManager.activeStage.scene.cursor = WAIT
            viewModel.saveAlumnadoToJson(it)
                .onSuccess {
                    showAlertOperacion(
                        title = "Datos exportados",
                        mensaje = "Se ha exportado tus Expedientes.\nAlumnos exportados: ${viewModel.state.value.alumnos.size}"
                    )
                }.onFailure { error ->
                    showAlertOperacion(alerta = AlertType.ERROR, title = "Error al exportar", mensaje = error.message)
                }
            RoutesManager.activeStage.scene.cursor = DEFAULT
        }
    }

    private fun onImportarAction() {
        logger.debug { "onImportarAction" }
        // Forma larga, muy Java
        //val fileChooser = FileChooser()
        //fileChooser.title = "Importar expedientes"
        //fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("JSON", "*.json"))
        //val file = fileChooser.showOpenDialog(RoutesManager.activeStage)

        // Forma Kotlin con run y let (scope functions)
        FileChooser().run {
            title = "Importar expedientes"
            extensionFilters.add(FileChooser.ExtensionFilter("JSON", "*.json"))
            showOpenDialog(RoutesManager.activeStage)
        }?.let {
            logger.debug { "onAbrirAction: $it" }
            showAlertOperacion(
                AlertType.INFORMATION,
                "Importando datos",
                "Importando datos Se sustituye la imagen por una imagen por defecto."
            )
            // Cambiar el cursor a espera
            RoutesManager.activeStage.scene.cursor = WAIT
            viewModel.loadAlumnadoFromJson(it)
                .onSuccess {
                    showAlertOperacion(
                        title = "Datos importados",
                        mensaje = "Se ha importado tus Expedientes.\nAlumnos importados: ${viewModel.state.value.alumnos.size}"
                    )
                }.onFailure { error ->
                    showAlertOperacion(alerta = AlertType.ERROR, title = "Error al importar", mensaje = error.message)
                }
            RoutesManager.activeStage.scene.cursor = DEFAULT
        }
    }

    private fun showAlertOperacion(
        alerta: AlertType = AlertType.CONFIRMATION,
        title: String = "",
        mensaje: String = ""
    ) {
        Alert(alerta).apply {
            this.title = title
            this.contentText = mensaje
        }.showAndWait()
    }

    private fun onUnzipAction() {
        logger.debug { "onUnzipAction" }
        FileChooser().run {
            title = "Importar desde Zip"
            extensionFilters.add(FileChooser.ExtensionFilter("ZIP", "*.zip"))
            showOpenDialog(RoutesManager.activeStage)
        }?.let {
            logger.debug { "onAbrirAction: $it" }
            showAlertOperacion(
                AlertType.INFORMATION,
                "Importando datos",
                "Importando datos. Se sustituye la imagen por la imagen encontrada en el zip."
            )
            // Cambiar el cursor a espera
            RoutesManager.activeStage.scene.cursor = WAIT
            viewModel.loadAlumnadoFromZip(it)
                .onSuccess {
                    showAlertOperacion(
                        title = "Datos importados",
                        mensaje = "Se ha importado tus Expedientes.\nAlumnos importados: ${viewModel.state.value.alumnos.size}"
                    )
                }.onFailure { error ->
                    showAlertOperacion(alerta = AlertType.ERROR, title = "Error al importar", mensaje = error.message)
                }
            RoutesManager.activeStage.scene.cursor = DEFAULT
        }

    }

    private fun onZipAction() {
        logger.debug { "onZipAction" }
        FileChooser().run {
            title = "Exportar a Zip"
            extensionFilters.add(FileChooser.ExtensionFilter("ZIP", "*.zip"))
            showSaveDialog(RoutesManager.activeStage)
        }?.let {
            logger.debug { "onAbrirAction: $it" }
            // Cambiar el cursor a espera
            RoutesManager.activeStage.scene.cursor = WAIT
            viewModel.exportToZip(it)
                .onSuccess {
                    showAlertOperacion(
                        title = "Datos exportados",
                        mensaje = "Se ha exportado tus Expedientes completos con imágenes.\nAlumnos exportados: ${viewModel.state.value.alumnos.size}"
                    )
                }.onFailure { error ->
                    showAlertOperacion(alerta = AlertType.ERROR, title = "Error al exportar", mensaje = error.message)
                }
            RoutesManager.activeStage.scene.cursor = DEFAULT
        }
    }

}
