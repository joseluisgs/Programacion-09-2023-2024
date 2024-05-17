package dev.joseluisgs.moscafx.mosca.errors

import dev.joseluisgs.moscafx.mosca.models.MoscaPosition

sealed class MoscaError {
    data object NoAcertado : MoscaError()
    class FinIntentos(val mosca: MoscaPosition, val intentos: Int) : MoscaError()
    class Casi(val fila: Int, val columna: Int, val intentos: Int) : MoscaError()
}