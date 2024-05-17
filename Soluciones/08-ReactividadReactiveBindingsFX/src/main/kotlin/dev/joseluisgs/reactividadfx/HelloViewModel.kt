package dev.joseluisgs.reactividadfx

import javafx.beans.property.SimpleObjectProperty
import org.lighthousegames.logging.logging

private val logger = logging()

class HelloViewModel {

    data class State(
        val contador: Int = 0,
        val mensaje: String = "",
        val lista: List<String> = listOf("Uno", "Dos", "Tres")
    )

    val state: SimpleObjectProperty<State> =
        SimpleObjectProperty(State()) // Ahora es observable el objeto, no sus propiedades

    init {
        logger.debug { "Modelo inicializado" }
    }

    fun incrementar() {
        logger.debug { "Incrementando contador" }
        state.value = state.value.copy(contador = state.value.contador + 1)
        if (state.value.contador == 10) {
            logger.debug { "Has llegado a 10" }
            state.value = state.value.copy(mensaje = "Has llegado a 10")
        }
        if (state.value.contador % 5 == 0) {
            logger.debug { "Has llegado a multiplo de 5" }
            state.value = state.value.copy(lista = state.value.lista + "Nuevo: ${state.value.contador}")
        }
    }

    fun cambiarMensaje(nuevoMensaje: String) {
        logger.debug { "Cambiando mensaje a $nuevoMensaje" }
        if (nuevoMensaje.contains("y")) {
            logger.debug { "El mensaje contiene 'y'" }
            state.value = state.value.copy(mensaje = nuevoMensaje.replace("y", ""))
        } else {
            logger.debug { "El mensaje no contiene 'y'" }
            state.value = state.value.copy(mensaje = nuevoMensaje)
        }
    }
}