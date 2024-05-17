module dev.joseluisgs.pokedexfx {
    // Librerías que vamos a usar
    // Java FX
    requires javafx.controls;
    requires javafx.fxml;
    // Kotlin
    requires kotlin.stdlib;
    // Logger
    requires logging.jvm;
    requires org.slf4j;
    // Result
    requires kotlin.result.jvm;
    // Koin
    requires koin.core.jvm;
    // Open Vadin
    requires open;
    // Kotlin Serialization
    requires kotlinx.serialization.core;
    requires kotlinx.serialization.json;


    // Abrimos y exponemos lo que va a usar desde clases con FXML
    opens dev.joseluisgs.pokedexfx to javafx.fxml;
    exports dev.joseluisgs.pokedexfx;

    // Rutas
    opens dev.joseluisgs.pokedexfx.routes to javafx.fxml;
    exports dev.joseluisgs.pokedexfx.routes;

    // Acerca de
    opens dev.joseluisgs.pokedexfx.acercade.controllers to javafx.fxml;
    exports dev.joseluisgs.pokedexfx.acercade.controllers;

    // Pokedex
    opens dev.joseluisgs.pokedexfx.pokedex.controllers to javafx.fxml;
    exports dev.joseluisgs.pokedexfx.pokedex.controllers;

    // modelos
    opens dev.joseluisgs.pokedexfx.pokedex.models to javafx.fxml;
    exports dev.joseluisgs.pokedexfx.pokedex.models;

    //dtos
    opens dev.joseluisgs.pokedexfx.pokedex.dto.json to javafx.fxml;
    exports dev.joseluisgs.pokedexfx.pokedex.dto.json;

}