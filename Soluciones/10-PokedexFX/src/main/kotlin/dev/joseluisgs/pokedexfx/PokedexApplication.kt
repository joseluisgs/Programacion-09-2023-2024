package dev.joseluisgs.pokedexfx

import dev.joseluisgs.pokedexfx.di.appModule
import dev.joseluisgs.pokedexfx.routes.RoutesManager
import javafx.application.Application
import javafx.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.lighthousegames.logging.logging

private val logger = logging()

class PokedexApplication : Application(), KoinComponent {

    override fun init() {
        // Cargo el contexto de Koin de DI
        startKoin {
            printLogger()
            modules(appModule)
        }
    }

    override fun start(stage: Stage) {
        logger.debug { "Iniciando PokedexFX App" }


        // Le pasamos la aplicación a la clase RoutesManager
        RoutesManager.apply {
            app = this@PokedexApplication
        }
        // Inicializamos la escena principal
        RoutesManager.initMainStage(stage)
    }
}

fun main() {

    Application.launch(PokedexApplication::class.java)
}