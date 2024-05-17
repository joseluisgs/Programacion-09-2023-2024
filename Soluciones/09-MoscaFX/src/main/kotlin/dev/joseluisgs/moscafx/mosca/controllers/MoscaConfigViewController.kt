package dev.joseluisgs.moscafx.mosca.controllers

import dev.joseluisgs.moscafx.mosca.viewmodel.MoscaViewModel
import dev.joseluisgs.moscafx.routes.RoutesManager
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.MenuItem
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging

private val logger = logging()

class MoscaConfigViewController : KoinComponent {
    // Mi ViewModel, debemos inyectarlo bien :) o hacer el get() de Koin si no lo queremos lazy
    val viewModel: MoscaViewModel by inject()

    // Menus
    @FXML
    private lateinit var menuSalir: MenuItem

    @FXML
    private lateinit var menuAcercaDe: MenuItem


    @FXML
    private lateinit var spinnerDimension: Spinner<Int>

    @FXML
    private lateinit var spinnerIntentos: Spinner<Int>

    // Botones
    @FXML
    lateinit var botonComenzar: Button


    @FXML
    fun initialize() {
        logger.debug { "Inicializando Mosca FXML y Controlador asociado" }
        initDefaultsValues() // Inicializamos valores por defecto
        initEvents() // Inicializamos eventos
    }


    private fun initDefaultsValues() {
        logger.debug { "Inicializando valores por defecto" }
        // Iniciamos los spinners
        spinnerDimension.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(3, 10, 3)
        spinnerIntentos.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 3)
    }

    private fun initEvents() {
        logger.debug { "Inicializando eventos" }
        // Eventos de los menus
        menuSalir.setOnAction { RoutesManager.onAppExit() }
        menuAcercaDe.setOnAction { onAcercaDeAction() }

        // Eventos de los botones
        botonComenzar.setOnAction { onComenzarAction() }
    }

    private fun onComenzarAction() {
        // Cogemos las dimensiones
        val dimension = spinnerDimension.value.toInt()
        val intentos = spinnerIntentos.value.toInt()
        // Iniciamos el juego
        viewModel.iniciarJuego(dimension, intentos)
        RoutesManager.changeScene(view = RoutesManager.View.JUEGO)
    }

    private fun onAcercaDeAction() {
        logger.debug { "abriendo Acerca de" }
        RoutesManager.initAcercaDeStage()
    }

}