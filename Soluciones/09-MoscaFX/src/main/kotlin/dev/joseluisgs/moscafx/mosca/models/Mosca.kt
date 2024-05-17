package dev.joseluisgs.moscafx.mosca.models

const val MOSCA = -1

data class MoscaPosition(val fila: Int, val columna: Int)

data class Acertado(val mosca: MoscaPosition, val intentos: Int)