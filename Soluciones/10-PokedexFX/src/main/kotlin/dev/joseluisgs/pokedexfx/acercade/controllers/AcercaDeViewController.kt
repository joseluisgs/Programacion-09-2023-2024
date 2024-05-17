package dev.joseluisgs.pokedexfx.acercade.controllers

import com.vaadin.open.Open
import javafx.fxml.FXML
import org.lighthousegames.logging.logging

private val logger = logging()

class AcercaDeViewController {
    @FXML
    private lateinit var linkGitHub: javafx.scene.control.Hyperlink

    // Inicializamos
    @FXML
    fun initialize() {
        logger.debug { "Inicializando AcercaDeViewController FXML" }
        linkGitHub.setOnAction {
            val url = "https://github.com/joseluisgs"
            logger.debug { "Abriendo navegador en el link: $url" }
            Open.open(url)
        }
    }
}