module dev.joseluisgs.reactividadfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires logging.jvm;
    requires org.slf4j;


    opens dev.joseluisgs.reactividadfx to javafx.fxml;
    exports dev.joseluisgs.reactividadfx;
}