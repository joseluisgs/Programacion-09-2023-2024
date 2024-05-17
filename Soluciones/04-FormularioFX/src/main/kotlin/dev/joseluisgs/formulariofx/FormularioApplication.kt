package dev.joseluisgs.formulariofx

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import org.lighthousegames.logging.logging

private val logger = logging()

class FormularioApplication : Application() {
    // Sobre escribimos el método start
    // Este método es obligatorio y lanza la aplicación: stage inicial
    override fun start(stage: Stage) {
        logger.debug { "Cargando vista" }
        // Cargamos el FXML
        val fxmlLoader = FXMLLoader(
            // Ojo al path siempre relativo a la carpeta resources desde donde se ejecuta
            // Cuidad con los paquetes
            { }::class.java.getResource("views/formulario-view.fxml")
        )

        // Creamos la escena
        val mainScene = Scene(fxmlLoader.load(), 500.0, 400.0)

        // Creamos la ventana
        logger.debug { "Creando ventana" }
        stage.apply {
            // Ponemos el tamaño mínimo, si queremos, sino no es necesario
            minWidth = mainScene.width
            minHeight = mainScene.height
            // Ponemos el título
            title = "Registro"
            // Ponemos la escena
            scene = mainScene
            // Le ponemos un icono
            icons.add(
                Image(
                    { }::class.java.getResourceAsStream("icons/app-icon.png")
                )
            )
        }.show() // Mostramos la ventana
    }

    // Opcionalmente podemos sobreescribir el método stop e init, si queremos hacer algo antes o después
    override fun stop() {
        println("Stop")
    }

    override fun init() {
        println("Init")
    }
}

fun main() {
    // Lanzamos la aplicación
    // Usamos el método launch de la clase Application, que llama al método start
    Application.launch(FormularioApplication::class.java)
}