package dev.joseluisgs.pokedexfx.pokedex.errors

sealed class PokedexErrors(val mensaje: String) {
    class LoadJsonError(mensaje: String) : PokedexErrors(mensaje)
}
