module dev.joseluisgs.expedientesacademicos {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Kotlin
    requires kotlin.stdlib;

    // Logger
    requires logging.jvm;
    requires org.slf4j;

    // Kotlin Serialization
    requires kotlinx.serialization.core;
    requires kotlinx.serialization.json;

    // Result
    requires kotlin.result.jvm;

    // SqlDeLight
    requires runtime.jvm;
    requires sqlite.driver;
    requires java.sql; // Como no pongas esto te vas a volver loco con los errores!!

    // Koin

    requires koin.core.jvm;
    // Open Vadin
    requires open;


    // Abrimos y exponemos el paquete a JavaFX
    opens dev.joseluisgs.expedientesacademicos to javafx.fxml;
    exports dev.joseluisgs.expedientesacademicos;

    // Rutas
    opens dev.joseluisgs.expedientesacademicos.routes to javafx.fxml;
    exports dev.joseluisgs.expedientesacademicos.routes;


    // Alumnado
    // Controllers
    opens dev.joseluisgs.expedientesacademicos.alumnado.controllers to javafx.fxml;
    exports dev.joseluisgs.expedientesacademicos.alumnado.controllers;

    // dtos
    opens dev.joseluisgs.expedientesacademicos.alumnado.dto.json to javafx.fxml;
    exports dev.joseluisgs.expedientesacademicos.alumnado.dto.json;

    // Modelos a javafx para poder usarlos en las vistas
    opens dev.joseluisgs.expedientesacademicos.alumnado.models to javafx.fxml;
    exports dev.joseluisgs.expedientesacademicos.alumnado.models;

    // Acerca de
    // Controllers
    opens dev.joseluisgs.expedientesacademicos.acercade.controllers to javafx.fxml;
    exports dev.joseluisgs.expedientesacademicos.acercade.controllers;

}