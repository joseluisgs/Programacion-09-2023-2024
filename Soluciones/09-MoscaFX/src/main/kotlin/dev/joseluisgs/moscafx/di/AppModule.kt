package dev.joseluisgs.moscafx.di

import dev.joseluisgs.moscafx.mosca.viewmodel.MoscaViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val AppModule = module {
    // Lo voy a definir todo como Singleton
    // https://insert-koin.io/docs/reference/koin-core/dsl
    singleOf(::MoscaViewModel) // B (A) --> Lo hace automáticamente
}
