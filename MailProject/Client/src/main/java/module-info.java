module org.mx.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;


    opens org.mx.client to javafx.fxml;
    opens org.mx.post.entities to javafx.base;
    exports org.mx.client;
    exports org.mx.client.controllers;
    opens org.mx.client.controllers to javafx.fxml;
}