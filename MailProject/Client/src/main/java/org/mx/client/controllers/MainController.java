package org.mx.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import org.mx.client.MainApplication;
import org.mx.client.services.SessionManager;
import org.mx.post.center.MailBox;
import org.mx.post.entities.Bag;
import org.mx.post.entities.Mail;

import java.io.IOException;
import java.util.Optional;

public class MainController {
    @FXML
    private TextField mailField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button loginButton;

    private SessionManager sessionManager;

    @FXML
    protected void onLoginButtonClick() {
        Bag<MailBox> responseBag;
        if(checkForm()){
            try {
                responseBag=sessionManager.handleLogin(mailField.getText(), passwordField.getText());
                if(responseBag.getResultCode()==Bag.resultCode.SUCCESS){
                    openHome(responseBag);
                }else{
                    showError("Check your credentials");
                }
            } catch (Exception e) {
                showError(e.getMessage() + ", cause: " + e.getCause());
            }



        }
    }

    private boolean checkForm(){
        if(mailField.getText().isBlank()||passwordField.getText().isBlank()){
            showAlert("Fill all the fields please");
            return false;
        }
        return true;
    }

    private void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle("Error");
        alert.setHeaderText("An error has happened");
        alert.setContentText(message);

        alert.showAndWait();
    }
    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("An error has happened");
        alert.setContentText(message);

        alert.showAndWait();
    }

    public void setSessionManager(SessionManager sessionManager){
        this.sessionManager=sessionManager;
    }

    private void openHome(Bag response) throws IOException {

        HomeController homeController = new HomeController();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("home-view.fxml"));
        fxmlLoader.setController(homeController);
        homeController.setSessionManager(sessionManager);

        Scene scene = new Scene(fxmlLoader.load());
        Stage secondStage = new Stage();
        secondStage.setTitle("Home");
        secondStage.setScene(scene);
        secondStage.show();

        homeController.welcomeBag(response);
        Stage thisStage = (Stage)loginButton.getScene().getWindow();
        thisStage.close();
    }
}