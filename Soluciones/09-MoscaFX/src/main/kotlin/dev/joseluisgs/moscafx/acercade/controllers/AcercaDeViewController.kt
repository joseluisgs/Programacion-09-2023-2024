package dev.joseluisgs.moscafx.acercade.controllers

import com.vaadin.open.Open
import javafx.fxml.FXML
import javafx.scene.control.Hyperlink
import org.lighthousegames.logging.logging

private val logger = logging()

class AcercaDeViewController {
    @FXML
    private lateinit var linkGitHub: Hyperlink

    // Inicializamos
    @FXML
    fun initialize() {
        logger.info { "Inicializando AcercaDeViewController FXML" }

        linkGitHub.setOnAction {
            val url = "https://github.com/joseluisgs"
            logger.debug { "Abriendo navegador en el link: $url" }
            Open.open(url)
        }
    }
}