module dev.joseluisgs.moscafx {
    // Librerías que vamos a usar
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    // Kotlin
    requires kotlin.stdlib;
    // Loggin
    requires logging.jvm;
    requires org.slf4j;
    // Koin
    requires koin.core.jvm;
    // Open de Vadin
    requires open;
    // Result
    requires kotlin.result.jvm;


    // Abrimos y exponemos lo que va a usar desde clases con FXML
    opens dev.joseluisgs.moscafx to javafx.fxml;
    exports dev.joseluisgs.moscafx;

    // Rutas
    opens dev.joseluisgs.moscafx.routes to javafx.fxml;
    exports dev.joseluisgs.moscafx.routes;

    // Mosca
    opens dev.joseluisgs.moscafx.mosca.controllers to javafx.fxml;
    exports dev.joseluisgs.moscafx.mosca.controllers;

    // Acerca de
    opens dev.joseluisgs.moscafx.acercade.controllers to javafx.fxml;
    exports dev.joseluisgs.moscafx.acercade.controllers;


}