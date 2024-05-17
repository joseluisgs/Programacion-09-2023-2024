package dev.joseluisgs.calculadorafx

import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.stage.Stage
import org.lighthousegames.logging.logging
import java.util.*

private val logger = logging()

class CalculadoraApp : Application() {
    override fun start(stage: Stage) {
        // Cargar la vista, con { }::class.java.getResource("views/calculadora-view.fxml")
        // que hace {} y ::class.java.getResource("views/calculadora-view.fxml")

        logger.debug { "Cargando vista" }
        val fxml = {}::class.java.getResource("views/calculadora-view.fxml")

        //CalculadoraApp::class.java.getResource("views/calculadora-view.fxml")

        val fxmlLoader = FXMLLoader(fxml)

        // Creamos la escena con la vista cargada
        val mainScene = Scene(fxmlLoader.load(), 320.0, 180.0)

        logger.debug { "Cargando Ventana Principal" }
        // Configuramos el stage (ventana) y la mostramos
        stage.apply {
            Locale.setDefault(Locale("es", "ES")) // Cambiamos el idioma a español
            title = "CalculadoraFX" // Título de la ventana
            scene = mainScene // Asignamos la escena


            // Cargamos el icono de la aplicación, es como Stream
            icons.add(Image({ }::class.java.getResourceAsStream("icons/app-icon.png")))


            centerOnScreen() // Centramos la ventana
            isResizable = false // No permitimos redimensionar la ventana
            isMaximized = false // No permitimos maximizar la ventana

            // si queremos hacer algo cuando se minimiza o restaura
            iconifiedProperty().addListener { _, _, newValue ->
                if (newValue) println("Ventana minimizada")
                else println("Ventana restaurada")
            }

            // si queremos hacer algo cuando se cierra la ventana
            setOnCloseRequest {
                println("Ventana cerrada")
                // Creamos un alerta de confirmación
                Alert(Alert.AlertType.CONFIRMATION).apply {
                    title = "Confirmar salida"
                    headerText = "¿Está seguro de que desea salir?"
                    contentText = "Si sale, la aplicación se cerrará."
                    // Ahora esperamos a que el usuario pulse una opción y si es OK salimos
                }.showAndWait().ifPresent { opcion ->
                    if (opcion == ButtonType.OK) {
                        println("Saliendo de la aplicación")
                        //exitProcess(0)
                        Platform.exit()
                    } else {
                        it.consume() // Consumir el evento para evitar que se cierre la ventana sin confirmación
                    }
                }
            }

        }.show()
    }

    // onStop, onDestroy, etc...
    override fun stop() {
        println("Aplicación cerrada")
    }

}

fun main() {
    Application.launch(CalculadoraApp::class.java)
}