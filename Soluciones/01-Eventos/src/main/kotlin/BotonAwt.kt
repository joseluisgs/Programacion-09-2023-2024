package dev.joseluisgs

import java.awt.event.ActionEvent
import java.awt.event.ActionListener

class ButtonListener : ActionListener {
    // Sobreescribimos el método que queremos usar
    override fun actionPerformed(e: ActionEvent) {
        println("Button clicked")
    }
}

// Creamos una clase que queremos observar,
class Button {
    // Lista de listeners
    private val actionListeners = mutableListOf<ActionListener>()

    fun addActionListener(listener: ActionListener) {
        actionListeners.add(listener)
    }

    fun removeActionListener(listener: ActionListener) {
        actionListeners.remove(listener)
    }

    // Ejecutamos el evento
    fun click() {
        actionListeners.forEach { it.actionPerformed(ActionEvent(this, 0, "click")) }
    }
}

fun main() {
    val button = Button()
    val listener = ButtonListener()
    button.addActionListener(listener)
    button.click()

    // o con lambda, sin necesidad de crear una clase
    button.addActionListener { println("Button clicked") }
}