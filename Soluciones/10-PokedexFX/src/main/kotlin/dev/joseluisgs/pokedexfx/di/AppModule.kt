package dev.joseluisgs.pokedexfx.di

import dev.joseluisgs.pokedexfx.pokedex.services.PokedexStorageJson
import dev.joseluisgs.pokedexfx.pokedex.viewmodel.PokedexViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    // Lo voy a definir todo como Singleton
    // https://insert-koin.io/docs/reference/koin-core/dsl
    singleOf(::PokedexStorageJson) // A
    singleOf(::PokedexViewModel) // B (A) --> Lo hace automáticamente

    //single { PokedexStorageJson() } // A
    //single { PokedexViewModel(get()) } // B (A) --> Lo hace automáticamente
}
