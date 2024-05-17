package dev.joseluisgs.routesmanagerfx.routes

import dev.joseluisgs.routesmanagerfx.controllers.HelloController
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
    private var acualStyle: Style = Style.DEFAULT

    // Podemos tener una cache de escenas cargadas
    private var scenesMap: HashMap<String, Pane> = HashMap()

    // Definimos las rutas de las vistas que tengamos
    enum class View(val fxml: String) {
        MAIN("views/hello-view.fxml"),
        ONE("views/one-view.fxml"),
        TWO("views/two-view.fxml")
    }

    // Definimos los estilos que tengamos
    enum class Style(val css: String) {
        DAW("styles/daw/daw.css"),
        DEFAULT("styles/modena/modena.css"),
        MODENA("styles/modena/modena.css"),
        BOOTSTRAP("styles/bootstrap/bootstrap3.css")
    }

    init {
        // Inicializamos el Locale, así el idioma de la aplicación
        Locale.setDefault(Locale.forLanguageTag("es-ES"))
    }

    // Recuerda
    // Si cambiamos de ventana, cambiamos una stage y una escena
    // Si mantenemos la ventana, cambiamos solo la escena

    // Inicializamos la scena principal
    fun initMainStage(stage: Stage) {
        logger.debug { "Inicializando la ventana principal" }
        // Configuramos todo como vienen por defecto
        val fxml = FXMLLoader(this.getResource(View.MAIN.fxml))

        // Creamos la escena con el fxml cargado y su tamaño
        val mainScene = Scene(fxml.load(), 320.0, 270.0)


        stage.apply {
            // Otros métodos para configurar el stage
            title = "Enrutador"
            // Podemos añadirles iconos o el estilo de cada ventana
            stage.icons.add(Image(getResourceAsStream("icons/java.png")))
            // incluso el evento de cerrar
            setOnCloseRequest {
                // Podemos hacer algo antes de cerrar
                onAppExit(event = it)
            }
            // No se puede redimensionar
            isResizable = false
            // Le asignamos la scena
            scene = mainScene

            // Guardamos la scena y el stage
            mainStage = stage
            // y es la actual
            _activeStage = stage

            // Cerramos la anterior
            // oldStage.close()

            centerOnScreen()

            // Mostramos la nueva
        }.show()

    }

    // Repetimos por cada stage configurando el stage y la escena como queramos
    //....

    // Abrimos one como una nueva ventana
    fun initPruebaEscenasStage() {
        logger.debug { "Inicializando la ventana de prueba" }
        // Configuramos todo como vienen por defecto
        val fxmlLoader = FXMLLoader(this.getResource(View.ONE.fxml))
        val parentRoot = fxmlLoader.load<Pane>() // otra forma de hacerlo
        // Fijamos su tamaño
        val myScene = Scene(parentRoot, 320.0, 240.0)

        val stage = Stage() // Creamos el stage, porque vamos a abrir una nueva ventana

        stage.apply {
            title = "Prueba de cambio de escena"
            scene = myScene
            // Otros métodos para configurar el stage
            // Podemos añadirles iconos o el estilo de cada ventana
            icons.add(Image(getResourceAsStream("icons/java.png")))
            // Vamos abrirla como modal, si queremos
            initOwner(mainStage)
            initModality(Modality.WINDOW_MODAL)
            // si vamos a querer cerrarla o ocultarla debemos salvarla
            // val oldStage = mainStage
            // Guardamos la scena y el stage
            // mainStage = stage
            // Cerramos la anterior
            // oldStage.close()
            // ahora la activa soy yo por ser modal
            _activeStage = stage
            // Mostramos la nueva
        }.show()

    }

    // O podemos hacer uno genérico, añade las opciones que necesites
    fun initStage(
        view: View,
        title: String = "Nueva ventana",
        modal: Boolean = false,
        icon: String = "",
        width: Double = 320.0,
        height: Double = 240.0,
        closePrevious: Boolean = true,
        style: Style = Style.DEFAULT
    ) {
        logger.debug { "Inicializando la ventana: $title" }
        // Configuramos todo como vienen por defecto
        val fxmlLoader = FXMLLoader(getResource(view.fxml))
        val parentRoot = fxmlLoader.load<Pane>()
        // Fijamos su tamaño
        val myScene = Scene(parentRoot, width, height)

        // Apliquemos el estilo
        if (style != Style.DEFAULT) {
            myScene.stylesheets.add(getResource(style.css).toExternalForm())
        }

        val stage = Stage()

        stage.apply {
            // Otros métodos para configurar el stage
            this.title = title
            scene = myScene
            // Podemos añadirles iconos o el estilo de cada ventana
            if (icon.isNotEmpty()) icons.add(Image(getResourceAsStream(icon)))
            // Vamos abrirla como modal
            if (modal) {
                initOwner(mainStage)
                initModality(Modality.WINDOW_MODAL)
            }
            if (closePrevious) {
                // si vamos a querer cerrarla o ocultarla debemos salvarla
                val oldStage = mainStage
                // Guardamos la scena y el stage
                mainStage = stage
                // Cerramos la anterior
                oldStage.close()
            }
            // ahora la activa soy yo
            _activeStage = stage
            // Mostramos
        }.show()

    }

    // Aquí definimos los FXML de cada scena, si no le pasamos nada es la activa
    fun changeScene(
        myStage: Stage = activeStage,
        view: View,
        width: Double = 320.0,
        height: Double = 240.0,
        style: Style = Style.DEFAULT
    ) {
        val parentRoot = FXMLLoader.load<Pane>(this.getResource(view.fxml))
        val scene = Scene(parentRoot, width, height)
        scene.stylesheets.add(this.getResource(style.css).toExternalForm())
        myStage.scene = scene
    }


    fun addScene(view: View) {
        scenesMap[view.fxml] = FXMLLoader.load(this.getResource(view.fxml))
    }

    fun removeScene(view: View) {
        scenesMap.remove(view.fxml)
    }

    fun activate(
        myStage: Stage = activeStage,
        view: View,
        width: Double = 320.0,
        height: Double = 240.0,
        style: Style = Style.DEFAULT
    ) {
        myStage.scene = Scene(scenesMap[view.fxml], width, height)
        myStage.scene.stylesheets.add(this.getResource(style.css).toExternalForm())
    }

    // Son publicos para poder acceder a ellos en cualquier momento
    fun getResource(resource: String): URL {
        return app::class.java.getResource(resource)
            ?: throw RuntimeException("No se ha encontrado el recurso: $resource")
    }

    // Para obtener un recurso como stream

    fun getResourceAsStream(resource: String): InputStream {
        return app::class.java.getResourceAsStream(resource)
            ?: throw RuntimeException("No se ha encontrado el recurso como stream: $resource")
    }

    // Para cambiar estilos
    fun changeStyle(myStage: Stage = activeStage, style: Style) {
        // Obtengo el nodo raíz de la scena
        val node = myStage.scene.root
        // Limpio los estilos
        node.scene.root.stylesheets.clear()

        when (style) {
            Style.DEFAULT -> {
                // Añado el estilo por defecto
                node.scene.stylesheets.add(this.getResource(Style.DEFAULT.css).toExternalForm())
            }

            else -> {
                // Añado el nuevo
                node.scene.stylesheets.add(this.getResource(style.css).toExternalForm())
            }
        }
    }

    fun onAppExit(title: String= "Salir de la aplicación",
                       headerText: String = "¿Estás seguro de que quieres salir?",
                       contentText: String = "Esto cerrará la aplicación actual",
                       event: WindowEvent? = null) {
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

