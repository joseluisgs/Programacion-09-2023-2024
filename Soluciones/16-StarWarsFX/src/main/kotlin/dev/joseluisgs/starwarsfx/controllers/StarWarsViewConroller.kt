package dev.joseluisgs.starwarsfx.controllers

import com.github.michaelbull.result.onSuccess
import dev.joseluisgs.starwarsfx.routes.RoutesManager
import dev.joseluisgs.starwarsfx.viewmodel.StarWarsResultado
import dev.joseluisgs.starwarsfx.viewmodel.StarWarsViewModel
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.Cursor
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.stage.FileChooser
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import kotlin.concurrent.thread


private val logger = logging()

class StarWarsViewConroller : KoinComponent {
    // Mi ViewModel, debemos inyectarlo bien :) o hacer el get() de Koin si no lo queremos lazy
    val viewModel: StarWarsViewModel by inject()

    // Menus
    @FXML
    private lateinit var menuSalir: MenuItem

    @FXML
    private lateinit var menuAcercaDe: MenuItem

    @FXML
    private lateinit var menuInforme: MenuItem

    // Spinners
    @FXML
    private lateinit var spinnerDimension: Spinner<Int>

    @FXML
    private lateinit var spinnerTiempo: Spinner<Int>

    @FXML
    private lateinit var spinnerDroides: Spinner<Int>

    // Areas de texto
    @FXML
    private lateinit var textOperacion: TextArea

    @FXML
    private lateinit var textAciertos: TextField

    @FXML
    private lateinit var textDisparos: TextField

    @FXML
    private lateinit var textMuertos: TextField

    @FXML
    private lateinit var textCuadrante: TextArea

    @FXML
    private lateinit var textTiempo: TextField

    // Botones
    @FXML
    lateinit var botonComenzar: Button

    @FXML
    lateinit var progressBar: ProgressBar


    @FXML
    fun initialize() {
        logger.debug { "Inicializando StarWars Controller FXML" }

        initDefaultValues()
        initEvents()
        initBindings()
    }

    private fun initBindings() {
        logger.debug { "initBindings" }
        // Hacemos el binding de los datos con el state
        viewModel.state.addListener { _, _, newValue ->
            // Aquí actualizamos la vista
            // ¿Por qué usamos runLater? Porque estamos en un hilo de JavaFX
            // piensa que en nuestra simulación hacemos cosas en background o en segundo plano
            // y al volver a entrar, debemos hacerlo en el hilo de JavaFX
            /*
            En JavaFX, la interfaz de usuario se actualiza en un hilo específico conocido como el hilo de JavaFX
            o hilo de la interfaz de usuario.
            Si intentas actualizar la interfaz de usuario desde un hilo que no sea el hilo de JavaFX,
            puedes experimentar problemas como bloqueos o excepciones.
            Al envolver las actualizaciones de la interfaz de usuario dentro de runLater, te aseguras de que estas
            actualizaciones se realicen en el hilo correcto.
            Al usar runLater, estás evitando bloque hilo de la interfaz de usuario y manteniendo la interfaz receptiva.
            Además, al hacer esto, también evitas posibles excepciones relacionadas con el acceso
            cruzado a la interfaz de usuario.
            Utilizar runLater para actualizar la interfaz de usuario es una buena práctica en JavaFX para garantizar
            la seguridad y el rendimiento de la aplicación.
            Asegura que todas las actualizaciones de la interfaz de usuario se realicen de manera segura y sin problemas de concurrencia.
            En resumen, al utilizar Platform.runLater en tu código, estás garantizando que las actualizaciones de la
            interfaz de usuario se realicen de manera segura y eficiente, evitando problemas de concurrencia y manteniendo la fluidez de la aplicación en JavaFX.
             */
            Platform.runLater {
                textOperacion.text = newValue.operacion
                textAciertos.text = newValue.aciertos.toString()
                textDisparos.text = newValue.disparos.toString()
                textMuertos.text = newValue.muertos.toString()
                textCuadrante.text = newValue.cuadrante
                textTiempo.text = newValue.tiempoActual.toString()
                progressBar.progress = newValue.tiempoActual.toDouble() / newValue.tiempoTotal.toDouble()
                textOperacion.positionCaret(textOperacion.text.length) // Ponemos el cursor al final del texto

                newValue.resultado.onSuccess {
                    when (it) {
                        is StarWarsResultado.StarWarsFin -> onTerminadoAction()
                        is StarWarsResultado.StarWarsLoading -> return@onSuccess // no hacemos nada o //onLoadingAction()

                    }
                }
            }
        }
    }

    private fun initEvents() {
        logger.debug {
            // Eventos de los menus
            menuSalir.setOnAction { onOnCloseAction() }
            menuAcercaDe.setOnAction { onAcercaDeAction() }
            menuInforme.setOnAction { onInformeAction() }

            // Eventos de los botones
            botonComenzar.setOnAction { onComenzarAction() }
        }
    }

    private fun initDefaultValues() {
        logger.debug { "initDefaultValues" }
        // Iniciamos los spinners
        spinnerDimension.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(5, 9, 5)
        spinnerDroides.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(5, 25, 10)
        spinnerTiempo.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(30, 200, 30)
    }

    private fun onComenzarAction() {
        logger.debug { "Comenzando simulación" }
        // Recogemos los valores de los spinners
        val dimension = spinnerDimension.value.toInt()
        val droides = spinnerDroides.value.toInt()
        val tiempo = spinnerTiempo.value.toInt()

        logger.debug { "Dimension: $dimension, Droides: $droides, Tiempo: $tiempo" }

        botonComenzar.isDisable = true

        // No dejamos actualizar el texto
        println("Comenzando simulación con $dimension, $droides, $tiempo")
        botonComenzar.scene.cursor = Cursor.WAIT // Cambiamos el cursor a espera, solo una vez

        // Comenzamos la simulación, esto debe ir en un hilo, al ser una tarea pesada
        // lo hacemos en un hilo, para no bloquear el hilo de JavaFX
        // Esto es porque la simulación es pesada y no queremos bloquear el hilo de JavaFX
        // No sabemos cuando termina, y hace cosas en background o segundo plano
        // Que a su vez van notificando cambios en la vista
        thread { viewModel.comenzarSimulacion(dimension, tiempo, droides) }

    }

    private fun onTerminadoAction() {
        logger.debug { "Terminando simulación" }
        // Mostramos un mensaje de que ha terminado
        // Le ponemos un runLater para que se ejecute en el hilo de JavaFX
        // y no en el hilo de la simulación, además así no bloqueamos el hilo
        // se hará cuando se pueda y haya terminado
        botonComenzar.scene.cursor = Cursor.DEFAULT
        botonComenzar.isDisable = false
        // viewModel.state.isTerminado.value = false
        Alert(AlertType.INFORMATION).apply {
            title = "Simulación terminada"
            headerText = "¡Simulación terminada!"
            contentText = "La simulación de la batalla ha terminado correctamente"
            showAndWait()
        }
        // Confirmamos para guardar el informe
        Alert(AlertType.CONFIRMATION).apply {
            title = "Guardar informe"
            headerText = "Salvar simulación"
            contentText = "¿Desea guardar el informe de la simulación?"

        }.showAndWait().ifPresent {
            if (it == ButtonType.OK) {
                onInformeAction()
            }
        }

    }

    private fun onInformeAction() {
        logger.debug { "onInformeAction" }
        // Guardamos el informe
        FileChooser().apply {
            title = "Guardar informe"
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("Text Files", "*.txt"),
            )
            showSaveDialog(RoutesManager.activeStage)?.let {
                viewModel.guardarInforme(it)
            }
        }
    }

    private fun onLoadingAction() {
        logger.debug { "onLoadingAction" }
        // Podríamos poner un cursor de espera o deshabilitar el botón o lo que queramos mientras
        // Esté ejecutando, pero ya lo hemos hecho antes
        //botonComenzar.scene.cursor = Cursor.WAIT
        // botonComenzar.isDisable = true
    }


    private fun onAcercaDeAction() {
        RoutesManager.initAcercaDeStage()
    }


    // Método para salir de la aplicación
    fun onOnCloseAction() {
        logger.debug { "onOnCloseAction" }
        RoutesManager.onAppExit()
    }
}
