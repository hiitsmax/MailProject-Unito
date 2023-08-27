package org.mx.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.Notifications;
import org.mx.client.controllers.MainController;
import org.mx.client.services.SessionManager;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MainController mainController = new MainController();
        SessionManager sessionManager = new SessionManager();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        fxmlLoader.setController(mainController);
        mainController.setSessionManager(sessionManager);

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        Notifications.create().position(Pos.TOP_CENTER).text("HHH").title("HHHH").showWarning();
    }


    public static void main(String[] args) {
        launch();
    }
}