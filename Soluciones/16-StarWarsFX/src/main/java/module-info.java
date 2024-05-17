module dev.joseluisgs.starwarsfx {
    // Librerías que vamos a usar
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    // Kotlin
    requires kotlin.stdlib;
    // Logger
    requires logging.jvm;
    requires org.slf4j;
    // Result
    requires kotlin.result.jvm;
    // Open
    requires open;
    // Koin
    requires koin.core.jvm;


    // Abrimos y exponemos lo que va a usar desde clases con FXML
    opens dev.joseluisgs.starwarsfx to javafx.fxml;
    exports dev.joseluisgs.starwarsfx;

    // Controladores
    opens dev.joseluisgs.starwarsfx.controllers to javafx.fxml;
    exports dev.joseluisgs.starwarsfx.controllers;

    // Rutas
    opens dev.joseluisgs.starwarsfx.routes to javafx.fxml;
    exports dev.joseluisgs.starwarsfx.routes;

    // modelos
    opens dev.joseluisgs.starwarsfx.models to javafx.fxml;
    exports dev.joseluisgs.starwarsfx.models;


}