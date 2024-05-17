package dev.joseluisgs.pokedexfx.pokedex.dto.json


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonJsonDto(
    @SerialName("avg_spawns")
    val avgSpawns: Double,
    @SerialName("candy")
    val candy: String,
    @SerialName("candy_count")
    val candyCount: Int? = null,
    @SerialName("egg")
    val egg: String,
    @SerialName("height")
    val height: String,
    @SerialName("id")
    val id: Int,
    @SerialName("img")
    val img: String,
    @SerialName("multipliers")
    val multipliers: List<Double>? = null,
    @SerialName("name")
    val name: String,
    @SerialName("next_evolution")
    val nextEvolution: List<NextEvolutionJsonDto>? = null,
    @SerialName("num")
    val num: String,
    @SerialName("prev_evolution")
    val prevEvolution: List<PrevEvolutionJsonDto>? = null,
    @SerialName("spawn_chance")
    val spawnChance: Double,
    @SerialName("spawn_time")
    val spawnTime: String,
    @SerialName("type")
    val type: List<String>,
    @SerialName("weaknesses")
    val weaknesses: List<String>,
    @SerialName("weight")
    val weight: String
)