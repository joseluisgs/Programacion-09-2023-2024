package dev.joseluisgs.moscafx.mosca.viewmodel

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.joseluisgs.moscafx.mosca.errors.MoscaError
import dev.joseluisgs.moscafx.mosca.models.Acertado
import dev.joseluisgs.moscafx.mosca.models.MOSCA
import dev.joseluisgs.moscafx.mosca.models.MoscaPosition
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import org.lighthousegames.logging.logging


private val logger = logging()


class MoscaViewModel {
    // Creo el estado de la vista que manejará el ViewModel
    val state = MoscaState()

    var matrix = Array(3) { IntArray(3) }

    init {
        logger.debug { "MoscaViewModel cargando datos" }
    }

    fun iniciarJuego(dimension: Int, intentos: Int) {
        logger.debug { "Iniciando juego con dimension $dimension e intentos $intentos" }
        state.apply {
            this.dimension = dimension
            this.intentos = intentos
            fila = 0
            columna = 0
            isTerminado.value = false
            cuadrante.value = ""
            golpes.set(0)
        }.also {
            // Inicializamos la matriz
            matrix = Array(dimension) { IntArray(dimension) }
            initMatrix()
            situarMosca()
        }
    }

    private fun situarMosca() {
        logger.debug { "Situando mosca" }
        // vamos a situar una mosca en una posición aleatoria
        // Debemos repetir mientras donde queremos situar la mosca ya esté ocupado
        var fila: Int
        var columna: Int
        do {
            fila = (matrix.indices).random()
            columna = (matrix.indices).random()
        } while (matrix[fila][columna] == MOSCA)
        // Ya tenemos la posición, la situamos
        matrix[fila][columna] = MOSCA
        // Actualizamos el estado
        state.moscaPosition = MoscaPosition(fila, columna)
        logger.debug { "La mosca está en la fila ${fila + 1} y columna ${columna + 1}" }
    }

    private fun initMatrix() {
        logger.debug { "Inicializando matriz" }
        // No es necesario, pero por que  ya lo hace!!!
        for (element in matrix) {
            for (j in matrix.indices) {
                element[j] = 0
            }
        }
    }

    fun golpear(fila: Int, columna: Int): Result<Acertado, MoscaError> {
        logger.debug { "Golpear $fila, $columna" }
        // Subimos los golpes
        state.golpes.value++
        // Se ha acabado el juego
        if (state.golpes.value == state.intentos) {
            state.isTerminado.value = true
            state.cuadrante.value = matrixToAreaFin()
            return Err(MoscaError.FinIntentos(state.moscaPosition, state.golpes.value))
        }

        // La has golpeando??
        if (matrix[fila][columna] == MOSCA) {
            state.isTerminado.value = true
            state.cuadrante.value = matrixToAreaFin()
            return Ok(Acertado(MoscaPosition(fila, columna), state.golpes.value))
        }

        // Es casi??
        // Nos hemos acercado al menos a 1 de distancia en una de las ocho direcciones
        if (state.moscaPosition.fila - fila in -1..1 && state.moscaPosition.columna - columna in -1..1) {
            state.cuadrante.value = matrixToAreaJuego()
            initMatrix()
            situarMosca()
            return Err(MoscaError.Casi(fila, columna, state.golpes.value))
        }
        state.cuadrante.value = matrixToAreaJuego()
        return Err(MoscaError.NoAcertado)
    }

    private fun matrixToAreaJuego(): String {
        val sb = StringBuilder()
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                sb.append("[   ]")
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    private fun matrixToAreaFin(): String {
        val sb = StringBuilder()
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (matrix[i][j] == MOSCA) {
                    // https://github.com/wooorm/gemoji/blob/main/support.md
                    sb.append("[ \uD83D\uDC1E ]") // Escape Unicode para el emoji de la mosca 🪰
                } else {
                    sb.append("[      ]")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }


    // Clase que representa el estado de la vista
    // Debes analizar los datos que necesitas para la vista
    // para poder realizar los bindings y que sean reactivos
    data class MoscaState(
        var dimension: Int = 0, // Tamaño de la matriz
        var intentos: Int = 0, // Número de intentos
        var fila: Int = 0, // Fila actual
        var columna: Int = 0, // Columna actual
        var isTerminado: SimpleBooleanProperty = SimpleBooleanProperty(false), // Binding en la IU
        val cuadrante: SimpleStringProperty = SimpleStringProperty(""), // binding en la IU
        val golpes: SimpleIntegerProperty = SimpleIntegerProperty(0), // Binding en la IU
        var moscaPosition: MoscaPosition = MoscaPosition(0, 0) // Posición de la mosca
    )
}
