module org.mx.server {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens org.mx.server to javafx.fxml;
    exports org.mx.server;
}