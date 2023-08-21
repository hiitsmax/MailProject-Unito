module org.mx.client {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens org.mx.client to javafx.fxml;
    exports org.mx.client;
}