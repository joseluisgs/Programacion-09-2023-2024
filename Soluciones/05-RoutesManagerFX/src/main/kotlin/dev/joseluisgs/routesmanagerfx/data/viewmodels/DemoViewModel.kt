package dev.joseluisgs.routesmanagerfx.data.viewmodels

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty

// usa Object si es compartido
object DemoViewModel {
    // Los datos que encapsulamos
    // Con esto ahora podemos enlazar el modelo con la vista, es observable
    val mensaje = SimpleStringProperty("Hola Mundo de 1 DAW")
    val numVeces = SimpleIntegerProperty(0)
}