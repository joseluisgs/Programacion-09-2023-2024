package dev.joseluisgs.pokedexfx.pokedex.dto.json


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NextEvolutionJsonDto(
    @SerialName("name")
    val name: String,
    @SerialName("num")
    val num: String
)