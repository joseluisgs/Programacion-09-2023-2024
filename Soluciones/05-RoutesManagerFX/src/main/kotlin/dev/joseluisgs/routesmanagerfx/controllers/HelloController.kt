package dev.joseluisgs.routesmanagerfx.controllers

import dev.joseluisgs.routesmanagerfx.data.parameters.DemoParameterState
import dev.joseluisgs.routesmanagerfx.data.viewmodels.DemoViewModel
import dev.joseluisgs.routesmanagerfx.routes.RoutesManager
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import org.lighthousegames.logging.logging
import java.util.*

private val logger = logging()

class HelloController {
    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private lateinit var botonIr: Button

    @FXML
    private lateinit var botonCambiarTema: Button

    @FXML
    private lateinit var textViewModel: TextField

    @FXML
    private lateinit var numVeces: Label

    @FXML
    private fun initialize() {
        logger.debug { "Inicializando la vista Hello" }
        welcomeText.text = "Hola Enrutador :)"
        // evento propios no definidos en el FXML
        botonIr.setOnAction { onBotonIrClick() }
        // Esta vez le voy a pasar el evento it = e -> ActionEvent
        botonCambiarTema.setOnAction { onBotonCambiarTemaClick(it) }
        // Enlazamos el modelo de datos a la vista, Si cambia el modelo cambia la vista y viceversa
        // Es decir, si cambia el texto en la vista cambia el modelo y viceversa
        //  prueba que pasa si lo pones unidireccional (bid)
        textViewModel.textProperty().bindBidirectional(DemoViewModel.mensaje)
        numVeces.textProperty().bind(DemoViewModel.numVeces.asString()) // no es bidireccional, porque no cambia

        // Tambien podemos observarlos y reaccionar a cambios

        DemoViewModel.mensaje.addListener { _, _, newValue ->
            println("Nuevo valor: $newValue")
        }

        DemoViewModel.numVeces.addListener { _, _, newValue ->
            println("Nuevo valor: $newValue")
        }
    }

    // Evento definido en el FXML
    @FXML
    private fun onHelloButtonClick() {
        logger.debug { "Botón pulsado" }
        welcomeText.text = "Esto es JavaFX y has pulsado el botón"
    }


    private fun onBotonIrClick() {
        println("Boton Ir pulsado")
        // antes de abrir relleno los datos a compartir
        // es una forma de pasar datos entre vistas o escenas
        // Pero es un poco engorroso y mejor usar un ViewModel para reactividad
        DemoParameterState.data = "Te mando esto desde Stage Prueba"
        DemoParameterState.number = 10


        RoutesManager.initPruebaEscenasStage() // llamamos al router para que abra la nueva ventana
    }

    private fun onBotonCambiarTemaClick(event: ActionEvent) {
        // voy a sacar los datos del evento por curiosidad
        println("Boton Cambiar pulsado cambiando de tema")
        println(event.toString())  // Evento podemos sacar el origen, el target, etc
        // Para cambiar el rema debemos hacer lo mismo que con la escena
        // es decir, detectar el stage y cambiar el tema
        RoutesManager.changeStyle(style = RoutesManager.Style.DAW)
        // Prueba con otros estilos
    }
}