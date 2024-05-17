package dev.joseluisgs.pokedexfx.pokedex.services


import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.joseluisgs.pokedexfx.pokedex.dto.json.PokedexJsonDto
import dev.joseluisgs.pokedexfx.pokedex.errors.PokedexErrors
import dev.joseluisgs.pokedexfx.pokedex.mappers.toPokemon
import dev.joseluisgs.pokedexfx.pokedex.models.Pokemon
import kotlinx.serialization.json.Json
import org.lighthousegames.logging.logging
import java.io.InputStream


private val logger = logging()

class PokedexStorageJson {

    fun loadPokedex(jsonFileStream: InputStream): Result<List<Pokemon>, PokedexErrors> {
        logger.debug { "Cargando Pokedex desde JSON: $jsonFileStream" }

        return try {
            val json = Json {
                ignoreUnknownKeys = true
            }
            val jsonString = jsonFileStream.reader().use { it.readText() }
            val pokedex = json.decodeFromString<PokedexJsonDto>(jsonString).listPokemonJsonDto.map { it.toPokemon() }
            return Ok(pokedex)


        } catch (e: Exception) {
            logger.error { "Error al cargar la Pokedex desde JSON: $jsonFileStream" }
            Err(PokedexErrors.LoadJsonError("Error al cargar la Pokedex desde JSON o fichero no es un Json valido\n ${e.message}"))
        }
    }
}