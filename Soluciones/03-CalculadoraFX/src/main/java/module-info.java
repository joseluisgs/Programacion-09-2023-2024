module dev.joseluisgs.calculadorafx {
    // Requiere significa que necesita de otro modulo para funcionar
    requires javafx.controls; // Requiere el modulo de controles de JavaFX
    requires javafx.fxml; // Requiere el modulo de FXML de JavaFX
    requires kotlin.stdlib;
    requires logging.jvm; // Requiere la librería de Kotlin
    requires org.slf4j; // Requiere la librería de Log4j

    // Abre el paquete para que pueda ser accedido desde el FXML
    opens dev.joseluisgs.calculadorafx to javafx.fxml;
    exports dev.joseluisgs.calculadorafx;

    // Abre el paquete para que pueda ser accedido desde el FXML
    opens dev.joseluisgs.calculadorafx.controllers to javafx.fxml;
    exports dev.joseluisgs.calculadorafx.controllers;
}