package dev.joseluisgs.pokedexfx.pokedex.controllers

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.joseluisgs.pokedexfx.pokedex.models.Pokemon
import dev.joseluisgs.pokedexfx.pokedex.viewmodel.PokedexViewModel
import dev.joseluisgs.pokedexfx.routes.RoutesManager
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.stage.FileChooser
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging


private val logger = logging()

class PokedexViewConroller : KoinComponent {

    // Mi ViewModel, debemos inyectarlo bien :) o hacer el get() de Koin si no lo queremos lazy
    val viewModel: PokedexViewModel by inject()

    // Menus
    @FXML
    private lateinit var menuSalir: MenuItem

    @FXML
    private lateinit var menuAcercaDe: MenuItem

    @FXML
    private lateinit var menuAbrir: MenuItem

    // Tabla
    @FXML
    private lateinit var tablePokemon: TableView<Pokemon>

    @FXML
    private lateinit var tableColumnNumero: TableColumn<Pokemon, String>

    @FXML
    private lateinit var tableColumNombre: TableColumn<Pokemon, String>

    // Combo y buscador y slider
    @FXML
    private lateinit var comboTipo: ComboBox<String>

    @FXML
    private lateinit var textBuscador: TextField

    // Formualrio de detalles
    @FXML
    private lateinit var imgPokemon: ImageView

    @FXML
    private lateinit var sliderPokemons: Slider

    @FXML
    private lateinit var textPokemonNumero: TextField

    @FXML
    private lateinit var textPokemonEvoluciones: TextArea

    @FXML
    private lateinit var textPokemonDebilidades: TextArea

    @FXML
    private lateinit var textPokemonPeso: TextField

    @FXML
    private lateinit var textPokemonAltura: TextField

    @FXML
    private lateinit var textPokemonNombre: TextField

    // Metodo para inicializar
    @FXML
    fun initialize() {
        logger.debug { "Inicializando AcercaDeViewController FXML" }

        initDefaultValues()
        initDefaultEvents()
        initReactiveProperties()
    }

    private fun initDefaultValues() {
        logger.debug { "initDefaultValues" }
        // Tabla
        tablePokemon.items =
            FXCollections.observableList(viewModel.state.value.pokemons) // Le pasamos la lista de pokemons
        // columnas, con el nombre de la propiedad del objeto hará binding
        tableColumnNumero.cellValueFactory = PropertyValueFactory("num")
        tableColumNombre.cellValueFactory = PropertyValueFactory("name")

        // comboBoxes
        comboTipo.items = FXCollections.observableList(viewModel.state.value.types) // Le pasamos la lista de tipos
        comboTipo.value = "All" // Selecciona ese valor

        // Atajos de teclado si queremos, se puede hacer con SceneBuilder
        menuAbrir.accelerator = KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN)
        menuSalir.accelerator = KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN)
        menuAcercaDe.accelerator = KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN)
    }

    private fun initDefaultEvents() {
        logger.debug { "initDefaultEvents" }
        // Botones
        menuSalir.setOnAction { RoutesManager.onAppExit() }
        menuAcercaDe.setOnAction { onAcercaDeAction() }
        menuAbrir.setOnAction { onAbrirAction() }
        // Tabla
        tablePokemon.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            newValue?.let { onTableSelected(it) }
        }
        // Evento del combo
        comboTipo.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            newValue?.let { onComboSelected(it) }
        }
        // Evento del buscador key press
        // Funciona con el intro
        // textBuscador.setOnAction {
        // con cualquer letra al levantarse, ya ha cambiado el valor
        textBuscador.setOnKeyReleased { newValue ->
            newValue?.let { onTextAction() }
        }
        // Slider
        sliderPokemons.min = 0.0
        sliderPokemons.max = viewModel.state.value.pokemons.size.toDouble() - 1
        sliderPokemons.valueProperty().addListener { _, _, newValue ->
            newValue?.let { onSliderSelected(it.toInt()) }
        }
    }

    private fun initReactiveProperties() {
        logger.debug { "initReactiveProperties" }
        // Reactividad de los campos, Ahora observamos solo el cambio en el estado
        viewModel.state.addListener { _, _, newState ->
            textPokemonNumero.text = newState.num
            textPokemonNombre.text = newState.name
            textPokemonAltura.text = newState.height
            textPokemonPeso.text = newState.weight
            textPokemonEvoluciones.text = newState.nextEvolution
            textPokemonDebilidades.text = newState.weaknesses
            imgPokemon.image = newState.img
        }
    }

    private fun onSliderSelected(newValue: Int) {
        // Ponemos el slider en la posición del pokemon seleccionado
        logger.debug { "onSliderSelected: $newValue" }
        tablePokemon.selectionModel.select(newValue) // Seleccionamos esta fila de la tabla
        tablePokemon.scrollTo(newValue) // Hacemos scroll para que se vea

    }

    private fun onTextAction() {
        // podemos cambiarlo en el binding diretamente
        logger.debug { "onTextAction: ${textBuscador.text}" }
        filterDataTable()
    }

    private fun onComboSelected(newValue: String) {
        // filtramos por el tipo seleccionado en la tabla
        logger.debug { "onComboSelected: $newValue" }
        filterDataTable()
    }

    private fun filterDataTable() {
        logger.debug { "filterDataTable" }
        // filtramos por el tipo seleccionado en la tabla
        // Cargo los datos filtrados
        tablePokemon.items = FXCollections.observableList(
            viewModel.pokemonFilteredList(comboTipo.value, textBuscador.text.trim())
        )

    }

    private fun onTableSelected(newValue: Pokemon) {
        logger.debug { "onTableSelected: $newValue" }
        viewModel.updateState(newValue)
    }

    private fun onAbrirAction() {
        // Abrimos el explorador de archivos
        FileChooser().apply {
            title = "Abrir Pokedex"
            extensionFilters.add(FileChooser.ExtensionFilter("JSON", "*.json"))
        }.showOpenDialog(RoutesManager.activeStage)?.let {
            logger.debug { "onAbrirAction: $it" }
            viewModel.loadPokedexFromJson(it)
                .onSuccess {
                    showAlertDatosCargados(mensaje = "Se ha importado tu Pokedex.\nPokemons cargados: ${viewModel.state.value.pokemons.size}")
                }.onFailure { error ->
                    showAlertDatosCargados(alerta = AlertType.ERROR, mensaje = error.mensaje)
                }
        }
    }

    private fun showAlertDatosCargados(alerta: AlertType = AlertType.CONFIRMATION, mensaje: String = "") {
        logger.debug { "showAlertDatosCargados: $mensaje" }
        when (alerta) {
            AlertType.ERROR -> {
                Alert(AlertType.ERROR).apply {
                    title = "Error al cargar datos"
                    contentText = mensaje
                }.showAndWait()
            }

            else -> {
                Alert(AlertType.CONFIRMATION).apply {
                    title = "Datos cargados"
                    contentText = mensaje
                }.showAndWait()
            }
        }
    }

    private fun onAcercaDeAction() {
        logger.debug { "onAcercaDeAction" }
        RoutesManager.initAcercaDeStage()
    }

}
