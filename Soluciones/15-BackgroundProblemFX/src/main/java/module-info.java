module dev.joseluisgs.backgroundproblem {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens dev.joseluisgs.backgroundproblem to javafx.fxml;
    exports dev.joseluisgs.backgroundproblem;
}