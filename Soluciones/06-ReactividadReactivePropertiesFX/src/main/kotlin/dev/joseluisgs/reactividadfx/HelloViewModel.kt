package dev.joseluisgs.reactividadfx

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.lighthousegames.logging.logging

private val logger = logging()

class HelloViewModel {

    data class State(
        val contador: SimpleIntegerProperty = SimpleIntegerProperty(0),
        val mensaje: SimpleStringProperty = SimpleStringProperty(""),
        val lista: ObservableList<String> = FXCollections.observableArrayList("Uno", "Dos", "Tres")
    )

    val state: State = State()

    init {
        logger.debug { "Modelo inicializado" }
    }

    fun incrementar() {
        logger.debug { "Incrementando contador" }
        state.contador.value++
        if (state.contador.value == 10) {
            logger.debug { "Has llegado a 10" }
            state.mensaje.value = "has llegado a lo máximo"
        }
        if (state.contador.value % 5 == 0) {
            logger.debug { "Has llegado a multiplo de 5" }
            state.lista.add("Nuevo ${state.contador.value}")
        }
    }

    fun cambiarMensaje(nuevoMensaje: String) {
        logger.debug { "Cambiando mensaje a $nuevoMensaje" }
        if (nuevoMensaje.contains("y")) {
            logger.debug { "El mensaje contiene 'y'" }
            state.mensaje.value = nuevoMensaje.replace("y", "")
        } else {
            logger.debug { "El mensaje no contiene 'y'" }
            state.mensaje.value = nuevoMensaje
        }
    }
}