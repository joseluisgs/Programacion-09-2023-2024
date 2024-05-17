package dev.joseluisgs.backgroundproblem

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ProgressBar
import kotlin.concurrent.thread

class HelloController {
    @FXML
    private lateinit var botonPrueba: Button

    @FXML
    private lateinit var barraPrueba: ProgressBar


    fun initialize() {
        println("Inicializando")
        botonPrueba.setOnAction {
            println("${Thread.currentThread().name} - Botón pulsado va a iniciar")

            /*
            Cuando realizamos una tarea en el hilo de la interfaz de usuario,
            el hilo se bloquea y no puede realizar otras tareas.
            Por lo tanto, si la tarea es larga (leer ficheros pesados, o una simulación), la interfaz de usuario se bloquea.
            Lo idea es realizar tareas pesadas en un hilo secundario y no en el hilo de la interfaz de usuario.
            Para ello, podemos utilizar la clase Thread
             */

            //playWithProgressBarBloqueando()
            //thread { playWithProgressBarJavaFXBloqueando() }
            thread { playWithProgressBarJafaFX() } // Esto es lo correcto
            println("${Thread.currentThread().name} - Botón pulsado ha terminado")
        }
    }

    private fun playWithProgressBarJavaFXBloqueando() {
        println("${Thread.currentThread().name} - Barra de progreso iniciando")
        botonPrueba.isDisable = true
        barraPrueba.progress = 0.0
        for (i in 0..100) {
            barraPrueba.progress = i / 100.0
            println("${Thread.currentThread().name} - Barra de progreso: $i")
            Thread.sleep(100) // Sleep bloquea el hilo actual
        }
        println("${Thread.currentThread().name} - Barra de progreso finalizado")
        botonPrueba.isDisable = false
    }

    private fun playWithProgressBarThread() {
        println("${Thread.currentThread().name} - Barra de progreso iniciando")
        botonPrueba.isDisable = true
        barraPrueba.progress = 0.0
        for (i in 0..100) {
            barraPrueba.progress = i / 100.0
            println("${Thread.currentThread().name} - Barra de progreso: $i")
            Thread.sleep(100) // Sleep bloquea el hilo actual
        }
        println("${Thread.currentThread().name} - Barra de progreso finalizado")
        botonPrueba.isDisable = false
    }

    private fun playWithProgressBarJafaFX() {
        println("${Thread.currentThread().name} - Barra de progreso iniciando")
        botonPrueba.isDisable = true
        barraPrueba.progress = 0.0
        for (i in 0..100) {
            /*
            Platform.runLater nos permite ejecutar un código en el hilo de la interfaz de usuario.
            Esto es necesario porque el hilo de la interfaz de usuario es el único que puede modificar la interfaz de usuario.
            Y como estamos en un hilo secundario, no podemos modificar la interfaz de usuario directamente.
             */
            Platform.runLater {
                barraPrueba.progress = i / 100.0
            }
            println("${Thread.currentThread().name} - Barra de progreso: $i")
            Thread.sleep(100) // Sleep bloquea el hilo actual
        }
        println("${Thread.currentThread().name} - Barra de progreso finalizado")
        botonPrueba.isDisable = false

    }


}