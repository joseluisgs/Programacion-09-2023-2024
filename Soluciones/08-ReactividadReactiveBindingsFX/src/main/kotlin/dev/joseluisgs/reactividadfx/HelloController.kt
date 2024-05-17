package dev.joseluisgs.reactividadfx

import javafx.beans.binding.Bindings
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
        // Podemos mapear el estado con las propiedades de la vista
        labelContador.textProperty().bind(viewModel.state.map { it.contador.toString() })
        // Tambien podemos usar Bindings para actualizar la vista en base al modelo
        labelMensaje.textProperty().bind(Bindings.select(viewModel.state, "mensaje"))
        // Tambien podemos usar Bindings para actualizar la vista en base al modelo
        //comboCosas.itemsProperty().bind(Bindings.select(viewModel.state, "lista"))
        textOtro.textProperty().bind(
            Bindings.select(
                viewModel.state,
                "mensaje"
            )
        ) // textOtro.textProperty().bind(viewModel.state.map { it.mensaje })

        // Ahora podemos escuchar solo los cambios en el contador
        viewModel.state.addListener { _, _, newValue ->
            if (comboCosas.items.size != newValue.lista.size || comboCosas.items != newValue.lista) {
                comboCosas.items.clear()
                comboCosas.items.addAll(newValue.lista)
            }

            if (newValue.contador == 10) {
                botonPulsar.isDisable = true
            }

        }
        // Tambien podemiamos haberlo hecho solo con el labelContador
        /*labelContador.textProperty().addListener { _, _, newValue ->
            if (newValue == "10") {
                botonPulsar.isDisable = true
            }
        }*/


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