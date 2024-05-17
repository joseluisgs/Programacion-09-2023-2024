package dev.joseluisgs.routesmanagerfx

import dev.joseluisgs.routesmanagerfx.routes.RoutesManager
import javafx.application.Application
import javafx.stage.Stage

class RoutesApplication : Application() {
    override fun start(stage: Stage) {
        // Le pasamos la aplicación a la clase RoutesManager
        RoutesManager.apply {
            app = this@RoutesApplication
            initMainStage(stage)
        }
    }
}

fun main() {
    Application.launch(RoutesApplication::class.java)
}