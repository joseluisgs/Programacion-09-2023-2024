package dev.joseluisgs

class MyButtonDirecto {
    // evento onClickListener, que es una función lambda
    private var onClickListener: ((MyButtonDirecto) -> Unit)? = null

    // Función para asignar un listener al botón
    // Ejecuta el listener, que es un lambda
    fun setOnClickListener(listener: (MyButtonDirecto) -> Unit) {
        this.onClickListener = listener
        this.onClickListener?.invoke(this)
    }
}

fun main() {
    // Creamos un botón
    val button = MyButtonDirecto()

    // Asignamos un listener al botón
    button.setOnClickListener {
        println("Button clicked!")
    }
}
