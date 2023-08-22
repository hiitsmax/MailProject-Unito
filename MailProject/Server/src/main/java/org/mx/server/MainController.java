package org.mx.server;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import org.mx.post.center.PostalCenter;
import org.mx.server.services.SessionManager;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Button newButton;
    @FXML
    private Button openButton;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private ListView<String> consoleList;

    private SessionManager sessionManager;
    private PostalCenter postalCenter;
    private ObservableList<String> logList;
    FileChooser fileChooser = new FileChooser();
    FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Postal center (*.post)", "*.post");

    @FXML
    protected void onNewButtonClick() {
        postalCenter = new PostalCenter();

        fileChooser.setTitle("Save a new postal center");
        File file = fileChooser.showSaveDialog(null);

        if(file!=null) savePostalCenter(file);
        logList.add("Created new postal center");

    }
    @FXML
    protected void onOpenButtonClick() {


        fileChooser.setTitle("Open a postal center");
        File file = fileChooser.showOpenDialog(null);

        if(file!=null) openPostalCenter(file);

    }
    @FXML
    protected void onStartButtonClick() {
        Platform.runLater(()->{
            sessionManager.start();
            disablePostalCenterButtons(true);
        });
    }
    @FXML
    protected void onStopButtonClick() {
        sessionManager.stop();
        disablePostalCenterButtons(false);
    }

    private void savePostalCenter(File file){
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))){
            objectOutputStream.writeObject(postalCenter);
            sessionManager.setPostalCenter(postalCenter);
            disableServerButtons(false);
        }catch (IOException e){
            showError(e.getMessage());
            logList.add("Error while creating postal center: "+e.getMessage());
        }
    }

    private void openPostalCenter(File file){
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            Object readObject = objectInputStream.readObject();
            if (readObject instanceof PostalCenter ps) {
                postalCenter = ps;
                sessionManager.setPostalCenter(ps);
            } else {
                showError("The file is not a postal center");
                logList.add("Error while opening postal center: The file is not a postal center");
            }
            logList.add("Opened postal center \""+file.getAbsolutePath()+"\"");
            disableServerButtons(false);
        } catch (IOException | ClassNotFoundException e) {
            showError(e.getMessage());
            logList.add("Error while opening postal center: "+e.getMessage());
        }
    }

    private void showError(String messageError){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("An error has happened");
        alert.setContentText(messageError);

        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileChooser.getExtensionFilters().add(extensionFilter);
        disableServerButtons(true);
    }

    public void setConsoleList(ObservableList<String> list){
        logList=list;
        consoleList.setItems(list);
    }

    private void disableServerButtons(Boolean state){
        startButton.setDisable(state);
        stopButton.setDisable(state);
    }
    private void disablePostalCenterButtons(Boolean state){
        newButton.setDisable(state);
        openButton.setDisable(state);
    }

    public void setSessionManager(SessionManager sessionManager){
        this.sessionManager=sessionManager;
    }
}