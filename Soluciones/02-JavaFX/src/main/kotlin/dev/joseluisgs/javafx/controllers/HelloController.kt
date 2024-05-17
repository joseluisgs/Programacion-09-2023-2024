package dev.joseluisgs.javafx.controllers

import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent

class HelloController {
    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private lateinit var cursoLabel: Label

    @FXML
    private lateinit var bienvenidaLabel: Label

    @FXML
    private lateinit var helloButton: Button

    private var clickCount = 0

    // Inicializamos los elementos
    @FXML
    fun initialize() {
        // Asociamos los eventos
        helloButton.setOnAction { onHelloButtonClick() }

        // Podemos cambiar cualquier propiedad
        bienvenidaLabel.text = "Hola 1 DAW"

        // Doble click en el botón y se crea el evento
        helloButton.onMouseClicked = EventHandler { event -> onDoubleClick(event) }

        // Evento de ratón
        cursoLabel.onMouseEntered = EventHandler { event -> onMouseEntered(event) }
        cursoLabel.onMouseExited = EventHandler { event -> onMouseExited(event) }
    }

    private fun onMouseExited(event: MouseEvent?) {
        if (clickCount in 1..5) {
            bienvenidaLabel.text = "Ha salido el ratón y has hecho $clickCount muchas entradas"
        } else {
            bienvenidaLabel.text = "Ha salido el ratón y has hecho muchas entradas"
        }
    }

    private fun onMouseEntered(event: MouseEvent?) {
        bienvenidaLabel.text = "Ha entrado el ratón"
        println("Ha entrado en el ratón")
        clickCount++
    }

    private fun onDoubleClick(event: MouseEvent) {
        if (event.clickCount == 2 && event.button.name == "SECONDARY") {
            bienvenidaLabel.text = "Doble click"
        }
    }


    private fun onHelloButtonClick() {
        bienvenidaLabel.text = "Welcome to JavaFX Application!"
    }

}