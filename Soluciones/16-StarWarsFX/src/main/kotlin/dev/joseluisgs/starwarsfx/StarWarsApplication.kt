package dev.joseluisgs.starwarsfx

import dev.joseluisgs.starwarsfx.di.appModule
import dev.joseluisgs.starwarsfx.routes.RoutesManager
import javafx.application.Application
import javafx.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.lighthousegames.logging.logging

private val logger = logging()

class StarWarsApplication : Application(), KoinComponent {

    override fun init() {
        logger.debug { "Iniciando Koin para PokedexFX App" }
        // Cargo el contexto de Koin de DI
        startKoin {
            printLogger() // Logger de Koin para ver lo que hace opcional
            modules(appModule)
        }

    }

    override fun start(stage: Stage) {
        logger.debug { "Iniciando PokedexFX App" }

        // Le pasamos la aplicación a la clase RoutesManager
        RoutesManager.apply {
            app = this@StarWarsApplication
        }
        // Inicializamos la escena principal
        RoutesManager.initMainStage(stage)
    }
}

fun main() {
    Application.launch(StarWarsApplication::class.java)
}