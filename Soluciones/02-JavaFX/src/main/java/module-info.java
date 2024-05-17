module dev.joseluisgs.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens dev.joseluisgs.javafx to javafx.fxml;
    exports dev.joseluisgs.javafx;

    opens dev.joseluisgs.javafx.controllers to javafx.fxml;
    exports dev.joseluisgs.javafx.controllers;
}