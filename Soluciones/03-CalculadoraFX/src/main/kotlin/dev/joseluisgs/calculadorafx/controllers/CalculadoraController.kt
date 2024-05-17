package dev.joseluisgs.calculadorafx.controllers

import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.TextField
import org.lighthousegames.logging.logging

private val logger = logging()

class CalculadoraController {
    // Elementos de la vista con los que vamos a interactuar
    @FXML
    private lateinit var textNumA: TextField

    @FXML
    private lateinit var textNumB: TextField

    @FXML
    private lateinit var textResultado: TextField

    @FXML
    private lateinit var botonSumar: Button

    @FXML
    private lateinit var botonRestar: Button

    @FXML
    private lateinit var botonMultiplicar: Button

    @FXML
    private lateinit var botonDividir: Button

    // Mi estado interno
    private var numA: Double = 0.0
    private var numB: Double = 0.0
    private var resultado: Double = 0.0

    @FXML
    fun initialize() {
        // Inicializamos los elementos de la vista y le ponemos valores por defecto
        // podríamos hacerlo en el FXML
        initDefaultValues()
        initEvents()
    }

    private fun initDefaultValues() {
        logger.debug { "Inicializando valores por defecto" }
        textNumA.text = "0.0"
        textNumB.text = "0.0"
        textResultado.text = "0.0"
    }

    private fun initEvents() {
        logger.debug { "Inicializando eventos" }
        // Eventos de los botones, los sacamos a las funciones correspondientes
        // por si son bastantes extensas
        botonSumar.setOnAction { onActionSumar() }
        botonRestar.setOnAction { onActionRestar() }
        botonMultiplicar.setOnAction { onActionMultiplicar() }
        botonDividir.setOnAction { onActionDividir() }
    }

    private fun onActionDividir() {
        logger.debug { "onActionDividir" }
        valideAndParseNumbers()
        if (numB == 0.0) {
            openErrorDialog("Error en B", "El valor introducido en B no puede ser 0")
            textNumB.text = "0.0"
            return
        }
        resultado = numA / numB
        textResultado.text = resultado.toString()

    }

    private fun onActionMultiplicar() {
        logger.debug { "onActionMultiplicar" }
        valideAndParseNumbers()
        resultado = numA * numB
        textResultado.text = resultado.toString()
    }

    private fun onActionRestar() {
        logger.debug { "onActionRestar" }
        valideAndParseNumbers()
        resultado = numA - numB
        textResultado.text = resultado.toString()
    }

    private fun onActionSumar() {
        logger.debug { "onActionSumar" }
        valideAndParseNumbers()
        resultado = numA + numB
        textResultado.text = resultado.toString()
    }

    private fun valideAndParseNumbers() {
        val parseA = textNumA.text.toDoubleOrNull()
        val parseB = textNumB.text.toDoubleOrNull()
        if (parseA == null) {
            openErrorDialog("Error en A", "El valor introducido en A no es un número")
            textNumA.text = "0.0"
            textResultado.text = "0.0"
            return
        }
        if (parseB == null) {
            openErrorDialog("Error en B", "El valor introducido en B no es un número")
            textNumB.text = "0.0"
            textResultado.text = "0.0"
            return
        }
        numA = parseA
        numB = parseB

    }

    private fun openErrorDialog(titulo: String, texto: String) {
        logger.debug { "openErrorDialog" }
        Alert(Alert.AlertType.ERROR).apply {
            title = titulo
            headerText = texto
            contentText = "Por favor, introduzca un número válido"
        }.showAndWait()
    }


}