package dev.joseluisgs.pokedexfx.pokedex.dto.json


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokedexJsonDto(
    @SerialName("pokemon")
    val listPokemonJsonDto: List<PokemonJsonDto>
)