package dev.joseluisgs.pokedexfx.pokedex.mappers

import dev.joseluisgs.pokedexfx.pokedex.dto.json.PokemonJsonDto
import dev.joseluisgs.pokedexfx.pokedex.models.Pokemon

fun PokemonJsonDto.toPokemon(): Pokemon {
    return Pokemon(
        id = this.id,
        num = this.num,
        name = this.name,
        height = this.height.removeSuffix("m").toDouble(),
        weight = this.weight.removeSuffix("kg").toDouble(),
        img = this.img.replace("http", "https"), // Cambiamos a https para evitar problemas de seguridad
        type = this.type,
        nextEvolution = this.nextEvolution?.map { it.name } ?: emptyList(),
        weaknesses = this.weaknesses
    )
}