package org.mx.server;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.mx.server.services.SessionManager;


import java.io.IOException;

public class MainApplication extends Application {
    SessionManager sessionManager;
    @Override
    public void start(Stage stage) throws IOException {

        ObservableList<String> logList = FXCollections.observableArrayList();
        sessionManager = new SessionManager();
        MainController mainController = new MainController();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        fxmlLoader.setController(mainController);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setOnCloseRequest(this::handleWindowClose);

        sessionManager.setLogList(logList);
        mainController.setConsoleList(logList);
        mainController.setSessionManager(sessionManager);

        stage.setScene(scene);
        stage.show();
    }
    private void handleWindowClose(WindowEvent event){
        if(sessionManager!=null) sessionManager.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}