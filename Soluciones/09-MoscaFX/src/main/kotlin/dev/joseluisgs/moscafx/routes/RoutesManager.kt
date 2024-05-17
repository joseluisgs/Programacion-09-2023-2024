package dev.joseluisgs.moscafx.routes

import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.WindowEvent
import org.lighthousegames.logging.logging
import java.io.InputStream
import java.net.URL
import java.util.*

private val logger = logging()

/**
 * Clase que gestiona las rutas de la aplicación
 */
object RoutesManager {
    // Necesitamos siempre saber
    private lateinit var mainStage: Stage // La ventana principal
    private lateinit var _activeStage: Stage // La ventana actual
    val activeStage: Stage
        get() = _activeStage
    lateinit var app: Application // La aplicación

    // Podemos tener una cache de escenas cargadas
    private var scenesMap: HashMap<String, Pane> = HashMap()

    // Definimos las rutas de las vistas que tengamos
    enum class View(val fxml: String) {
        MAIN("views/mosca-config-view.fxml"),
        JUEGO("views/mosca-view.fxml"),
        ACERCA_DE("views/acerca-de-view.fxml"),
    }


    init {
        logger.debug { "Inicializando RoutesManager" }
        // Podemos configurar el idioma de la aplicación aquí
        Locale.setDefault(Locale.forLanguageTag("es-ES"))
    }

    // Recuerda
    // Si cambiamos de ventana, cambiamos una stage y una scena
    // Si mantenemos la ventana, cambiamos solo la scena

    // Inicializamos la scena principal
    fun initMainStage(stage: Stage) {
        logger.debug { "Inicializando MainStage" }

        val fxmlLoader = FXMLLoader(getResource(View.MAIN.fxml))
        val parentRoot = fxmlLoader.load<Pane>()
        val mainScene = Scene(parentRoot, 875.0, 350.0)

        stage.apply {
            title = "La Mosca: El Juego"
            isResizable = false
            icons.add(Image(getResourceAsStream("icons/app.png")))
            scene = mainScene
            setOnCloseRequest {
                // Podemos hacer algo antes de cerrar
                // println("Cerrando la ventana")
                onAppExit(event = it)
            }

        }.show().also {
            mainStage = stage
            _activeStage = stage
        }
    }

    // Repetimos por cada stage configurando el stage y la scena
    //....

    // Abrimos one como una nueva ventana
    fun initAcercaDeStage() {
        logger.debug { "Inicializando AcercaDeStage" }

        val fxmlLoader = FXMLLoader(getResource(View.ACERCA_DE.fxml))
        val parentRoot = fxmlLoader.load<Pane>()
        val myScene = Scene(parentRoot, 350.0, 155.0)
        Stage().apply {
            title = "Acerca De Mosca FX"
            scene = myScene
            initOwner(mainStage)
            initModality(Modality.WINDOW_MODAL)
            isResizable = false
            icons.add(Image(getResourceAsStream("icons/app.png")))
        }.show()
    }

    fun changeScene(
        myStage: Stage = activeStage,
        view: View,
        width: Double = 875.0,
        height: Double = 350.0,
    ) {
        logger.debug { "Cambiando de escena a ${view.fxml}" }
        val parentRoot = FXMLLoader.load<Pane>(this.getResource(view.fxml))
        val scene = Scene(parentRoot, width, height)
        myStage.scene = scene
    }

    // O podemos hacer uno genérico, añade las opciones que necesites
    fun getResource(resource: String): URL {
        return app::class.java.getResource(resource)
            ?: throw RuntimeException("No se ha encontrado el recurso: $resource")
    }

    fun getResourceAsStream(resource: String): InputStream {
        return app::class.java.getResourceAsStream(resource)
            ?: throw RuntimeException("No se ha encontrado el recurso como stream: $resource")
    }

    fun onAppExit(
        title: String = "Salir de ${mainStage.title}?",
        headerText: String = "¿Estás seguro de que quieres salir de ${mainStage.title}?",
        contentText: String = "Si sales, se cerrará la aplicación y perderás todos los datos no guardados",
        event: WindowEvent? = null
    ) {
        logger.debug { "Cerrando formulario" }
        // Cerramos la aplicación
        Alert(Alert.AlertType.CONFIRMATION).apply {
            this.title = title
            this.headerText = headerText
            this.contentText = contentText
        }.showAndWait().ifPresent { opcion ->
            if (opcion == ButtonType.OK) {
                //exitProcess(0)
                Platform.exit()
            } else {
                event?.consume()
            }
        }
    }
}

