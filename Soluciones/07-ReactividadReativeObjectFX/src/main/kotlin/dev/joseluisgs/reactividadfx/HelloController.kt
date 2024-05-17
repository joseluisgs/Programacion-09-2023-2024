package dev.joseluisgs.reactividadfx

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import org.lighthousegames.logging.logging

private val logger = logging()


class HelloController {

    @FXML
    lateinit var labelContador: Label

    @FXML
    lateinit var botonPulsar: Button

    @FXML
    lateinit var textInput: TextField

    @FXML
    lateinit var labelMensaje: Label

    @FXML
    lateinit var comboCosas: ComboBox<String>

    @FXML
    lateinit var textOtro: TextField

    // MODELO
    val viewModel = HelloViewModel()


    @FXML
    private fun initialize() {
        initDefaultValues()
        initDefaultEvents()
        initBindings()
    }

    private fun initBindings() {
        // Ahora solo debemos escuchar el cambio del modelo
        viewModel.state.addListener { _, _, newValue ->
            // ahora solo actualizamos la vista
            labelContador.text = newValue.contador.toString()
            labelMensaje.text = newValue.mensaje
            comboCosas.items.clear()
            comboCosas.items.addAll(newValue.lista)
            textOtro.text = newValue.mensaje

            if (newValue.contador == 10) {
                botonPulsar.isDisable = true
            }
        }

    }

    private fun initDefaultEvents() {
        botonPulsar.setOnAction {
            logger.debug { "Botón pulsado" }
            viewModel.incrementar() // tocamos el modelo en base a la vista
        }

        textInput.textProperty().addListener { _, _, newValue ->
            logger.debug { "Texto cambiado a $newValue" }
            // ignoramos el cambio si hay una "y" y no actualiza el modelo
            viewModel.cambiarMensaje(newValue)
        }

        textOtro.textProperty().addListener { _, _, newValue ->
            logger.debug { "Texto cambiado a $newValue" }
            // ignoramos el cambio si hay una "y" y no actualiza el modelo
            viewModel.cambiarMensaje(newValue)
        }
    }

    private fun initDefaultValues() {
        labelContador.text = viewModel.state.value.contador.toString() // Inicializamos la vista con el modelo
        labelMensaje.text = viewModel.state.value.mensaje // Inicializamos la vista con el modelo
        textInput.text = ""
        comboCosas.items.addAll(viewModel.state.value.lista) // Inicializamos la vista con el modelo
    }
}