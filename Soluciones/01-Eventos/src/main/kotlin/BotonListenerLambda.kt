package dev.joseluisgs

class MyButtonLambda {
    // evento onClickListener, que es una función lambda
    private var onClickListener: ((MyButtonLambda) -> Unit)? = null

    // Función para asignar un listener al botón
    fun setOnClickListener(listener: (MyButtonLambda) -> Unit) {
        this.onClickListener = listener
    }

    // Función para simular el click en el botón
    // Ejecuta el listener, que es un lambda
    fun click() {
        onClickListener?.invoke(this)
    }
}

fun main() {
    // Creamos un botón
    val button = MyButtonLambda()

    // Asignamos un listener al botón
    // Es decir, le decimos que hacer cuando se haga click
    button.setOnClickListener {
        println("Button clicked!")
    }

    // Simulamos el click en el botón
    button.click()
}