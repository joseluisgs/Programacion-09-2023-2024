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
        // Enlazamos la vista con el modelo, cualquier cambio en el modelo se reflejará en la vista
        labelContador.textProperty().bind(viewModel.state.contador.asString())
        labelMensaje.textProperty().bind(viewModel.state.mensaje)
        comboCosas.itemsProperty().value = viewModel.state.lista
        textOtro.textProperty().bindBidirectional(viewModel.state.mensaje)

        // Pero tambien puedo programar listener para que se refleje en el modelo
        viewModel.state.contador.addListener { _, _, newValue ->
            logger.debug { "Contador cambiado a $newValue" }
            if (viewModel.state.contador.value == 10) {
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
    }

    private fun initDefaultValues() {
        labelContador.text = viewModel.state.contador.value.toString() // Inicializamos la vista con el modelo
        labelMensaje.text = viewModel.state.mensaje.value // Inicializamos la vista con el modelo
        textInput.text = ""
        comboCosas.items.addAll(viewModel.state.lista) // Inicializamos la vista con el modelo
    }
}