package dev.joseluisgs.pokedexfx.pokedex.viewmodel

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.onSuccess
import dev.joseluisgs.pokedexfx.locale.toLocalNumber
import dev.joseluisgs.pokedexfx.pokedex.errors.PokedexErrors
import dev.joseluisgs.pokedexfx.pokedex.models.Pokemon
import dev.joseluisgs.pokedexfx.pokedex.services.PokedexStorageJson
import dev.joseluisgs.pokedexfx.routes.RoutesManager
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import org.lighthousegames.logging.logging
import java.io.File

private val logger = logging()


class PokedexViewModel(
    private val pokedexStorageJson: PokedexStorageJson // Inyectamos el servicio
) {

    // Creo el estado con la imagen inicial
    val state: SimpleObjectProperty<PokemonState> = SimpleObjectProperty(PokemonState())

    init {
        logger.debug { "PokedexViewModel cargando datos" }
        // Cargamos los datos del directorio de recursos de la aplicación
        pokedexStorageJson.loadPokedex(RoutesManager.getResourceAsStream("data/pokedex.json"))
            .onSuccess {
                logger.debug { "PokedexViewModel datos cargados" }
                // Si todo va bien, los cargamos en la lista
                initState(it) // Inicializamos los observables
                logger.debug { "PokedexViewModel datos cargados pokedex: ${state.value.pokemons.size}" }
            }
    }

    fun loadPokedexFromJson(fichero: File): Result<List<Pokemon>, PokedexErrors> {
        logger.debug { "PokedexViewModel cargando datos desde fichero: ${fichero.absolutePath}" }
        return pokedexStorageJson.loadPokedex(fichero.inputStream())
            .onSuccess {
                initState(it) // Inicializamos los observables
                logger.debug { "PokedexViewModel datos cargados pokedex: ${state.value.pokemons.size}" }
            }
    }

    private fun initState(pokemonsList: List<Pokemon>) {
        // Inicializamos la lista de todos. Esto simplemente hago apply para no escribir distintas
        state.value = state.value.copy(
            pokemons = pokemonsList, // Lista de todos los pokemons
            types = listOf("All") + pokemonsList.map { it.type }
                .flatten() // Lista de tipos, todas las listas de tipos en una
                .distinct() // Lista de tipos, eliminamos duplicados
                .sorted() // Ordenamos
        )
    }

    fun updateState(pokemon: Pokemon) {
        logger.debug { "Actualizando estado de Pokemon: $pokemon" }
        // Actualizamos el estado con el Pokemon seleccionado
        state.value = state.value.copy(
            num = pokemon.num,
            name = pokemon.name,
            height = pokemon.height.toLocalNumber(),
            weight = pokemon.weight.toLocalNumber(),
            nextEvolution = pokemon.nextEvolution.joinToString { it },
            weaknesses = pokemon.weaknesses.joinToString { it },
            // Si no tiene imagen, la ponemos por defecto
            img = try {
                logger.debug { "Cargando imagen: ${pokemon.img}" }
                Image(pokemon.img)
            } catch (e: Exception) {
                logger.error { "Error al cargar la imagen: ${e.message}, usando imagen por defecto" }
                Image(RoutesManager.getResourceAsStream("images/sin-image.png"))
            }
        )

    }

    fun pokemonFilteredList(tipo: String, texto: String): List<Pokemon> {
        logger.debug { "Filtrando lista de Pokemon: $tipo, $texto" }

        return state.value.pokemons
            .filter { pokemon ->
                if (tipo == "All") true else pokemon.type.contains(tipo)
            }.filter { pokemon ->
                pokemon.name.contains(texto, true)
            }

    }

    // Clase que representa el estado de la vista
    data class PokemonState(
        val num: String = "",
        val name: String = "",
        val height: String = "",
        val weight: String = "",
        val nextEvolution: String = "",
        val weaknesses: String = "",
        val img: Image = Image(RoutesManager.getResourceAsStream("images/sin-image.png")),
        val types: List<String> = listOf(),
        val pokemons: List<Pokemon> = listOf(),
    )
}
