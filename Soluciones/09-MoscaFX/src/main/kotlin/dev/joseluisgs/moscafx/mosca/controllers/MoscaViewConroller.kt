package dev.joseluisgs.moscafx.mosca.controllers

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.joseluisgs.moscafx.mosca.errors.MoscaError
import dev.joseluisgs.moscafx.mosca.models.Acertado
import dev.joseluisgs.moscafx.mosca.viewmodel.MoscaViewModel
import dev.joseluisgs.moscafx.routes.RoutesManager
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging


private val logger = logging()

class MoscaViewConroller : KoinComponent {
    // Mi ViewModel, debemos inyectarlo bien :) o hacer el get() de Koin si no lo queremos lazy
    val viewModel: MoscaViewModel by inject()

    // Menus
    @FXML
    private lateinit var menuSalir: MenuItem

    @FXML
    private lateinit var menuAcercaDe: MenuItem

    @FXML
    private lateinit var spinnerFila: Spinner<Int>

    @FXML
    private lateinit var spinnerColumna: Spinner<Int>

    @FXML
    private lateinit var textCuadrante: TextArea

    @FXML
    private lateinit var textGolpes: TextField

    @FXML
    lateinit var botonGolpear: Button

    @FXML
    lateinit var botonComenzar: Button


    @FXML
    fun initialize() {
        logger.debug { "Inicializando Mosca FXML y Controlador asociado" }
        initDefaultsValues() // Inicializamos valores por defecto
        initEvents() // Inicializamos eventos
        initBindings() // Inicializamos bindings

    }

    private fun initDefaultsValues() {
        logger.debug { "Inicializando valores por defecto" }
        spinnerFila.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, viewModel.state.dimension, 1)
        spinnerColumna.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, viewModel.state.dimension, 1)
    }

    private fun initBindings() {
        logger.debug { "Inicializando bindings" }
        // Binding del cuadrante, es uni-direccional
        textCuadrante.textProperty().bind(viewModel.state.cuadrante)

        // Binding de los golpes es uni-direccional
        textGolpes.textProperty().bind(viewModel.state.golpes.asString())

        // esto de los botones lo voy a resolver reactivamente, ojo que podría haberlo hecho en cada método
        viewModel.state.isTerminado.addListener { _, _, newValue ->
            if (newValue) {
                botonGolpear.isDisable = true
                botonComenzar.isVisible = true
            }
        }
    }

    private fun initEvents() {
        logger.debug { "Inicializando eventos" }
        // Eventos de los menus
        menuSalir.setOnAction { RoutesManager.onAppExit() }
        menuAcercaDe.setOnAction { onAcercaDeAction() }
        botonGolpear.setOnAction { onGolpearAction() }
        botonComenzar.setOnAction { onComenzarAction() }
    }

    private fun onComenzarAction() {
        logger.debug { "Comenzando" }
        RoutesManager.changeScene(view = RoutesManager.View.MAIN)
    }


    private fun onGolpearAction() {
        logger.debug { "Golpeando" }
        // Cogemos las fila y columna
        val fila = spinnerFila.value.toInt() - 1
        val columna = spinnerColumna.value.toInt() - 1
        // Golpeamos
        viewModel.golpear(fila, columna).onSuccess {
            accionAcertar(it)
        }.onFailure {
            accionFallar(it)
        }
    }

    private fun accionFallar(error: MoscaError) {
        logger.debug { "Acción de fallo: $error" }
        when (error) {
            is MoscaError.FinIntentos -> finIntentosAction()
            is MoscaError.NoAcertado -> noAcertadoAction()
            is MoscaError.Casi -> casiAction(error)
        }
    }

    private fun casiAction(error: MoscaError.Casi) {
        logger.debug { "Casi: $error" }
        Alert(AlertType.WARNING).apply {
            title = "Casi lo has logrado"
            contentText =
                "¡Casi lo has logrado!\nLa mosca estaba cerca de la posición:\nFila: ${error.fila + 1}\nColumna: ${error.columna + 1}.\nLlevas ${error.intentos} intentos.\nLa mosca se ha movido a una nueva posición"
        }.showAndWait()
    }

    private fun noAcertadoAction() {
        logger.debug { "No acertado" }
        Alert(AlertType.WARNING).apply {
            title = "No acertado"
            contentText = "¡Maldita Mosca!. No has acertado, sigue intentándolo"
        }.showAndWait()
    }

    private fun finIntentosAction() {
        logger.debug { "Fin de intentos" }
        Alert(AlertType.ERROR).apply {
            title = "Fin de intentos"
            contentText =
                "¡Vaya!\nSe han acabado los intentos, la mosca estaba en la posición\nFila: ${viewModel.state.moscaPosition.fila + 1}\nColumna: ${viewModel.state.moscaPosition.columna + 1}"

        }.showAndWait().also {
            // si no quisieramos la reactividad podriamos haberlo resuleto los botones
            //  botonGolpear.isVisible = false
            //  botonComenzar.isVisible = true
        }
    }

    private fun accionAcertar(acierto: Acertado) {
        logger.debug { "Acción de acerto: $acierto" }
        Alert(AlertType.INFORMATION).apply {
            title = "Acertado"
            contentText =
                "¡Has acertado!\nLa mosca estaba en la posición\nFila: ${acierto.mosca.fila + 1}\nColumna: ${acierto.mosca.columna + 1}.\nHas necesitado ${acierto.intentos} intentos"

        }.showAndWait().also {
            // si no queremos reactividad
            //  botonGolpear.isVisible = false
            //  botonComenzar.isVisible = true
        }
    }

    private fun onAcercaDeAction() {
        logger.debug { "abriendo Acerca de" }
        RoutesManager.initAcercaDeStage()
    }

}
