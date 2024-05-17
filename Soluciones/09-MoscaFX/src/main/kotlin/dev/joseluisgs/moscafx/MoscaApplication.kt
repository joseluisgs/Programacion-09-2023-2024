package dev.joseluisgs.moscafx

import dev.joseluisgs.moscafx.di.AppModule
import dev.joseluisgs.moscafx.routes.RoutesManager
import javafx.application.Application
import javafx.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.lighthousegames.logging.logging

private val logger = logging()

class MoscaApplication : Application(), KoinComponent {
    override fun start(stage: Stage) {
        logger.info { "Iniciando MoscaFx App" }

        // Cargo el contexto de Koin de DI
        startKoin {
            printLogger()
            modules(AppModule)
        }


        // Le pasamos la aplicación a la clase RoutesManager
        // Inicializamos la ventana principal
        RoutesManager.apply {
            app = this@MoscaApplication
            initMainStage(stage)
        }
    }
}

fun main() {
    Application.launch(MoscaApplication::class.java)
}