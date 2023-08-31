package org.mx.client.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.mx.client.MainApplication;
import org.mx.client.services.SessionManager;
import org.mx.post.center.MailBox;
import org.mx.post.entities.Bag;
import org.mx.post.entities.Mail;
import org.mx.client.services.RefreshMailbox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private TextField mailField;
    @FXML
    private TextField passwordField;
    @FXML
    private TableView inboxTable;
    @FXML
    private TableView sentTable;
    @FXML
    private TableColumn<Mail, ArrayList<String>> fromInboxColumn;
    @FXML
    private TableColumn<Mail, ArrayList<String>> subjectInboxColumn;
    @FXML
    private TableColumn<Mail, String> bodyInboxColumn;
    @FXML
    private TableColumn<Mail, ArrayList<String>> fromSentColumn;
    @FXML
    private TableColumn<Mail, ArrayList<String>> subjectSentColumn;
    @FXML
    private TableColumn<Mail, String> bodySentColumn;

    private SessionManager sessionManager;
    private MailBox mailBox;



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

    private void fullfillInbox(){

        fromInboxColumn.setCellValueFactory(new PropertyValueFactory<>("From"));
        subjectInboxColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        bodyInboxColumn.setCellValueFactory(new PropertyValueFactory<>("body"));
        fromInboxColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Mail, ArrayList<String>> call(TableColumn<Mail, ArrayList<String>> column) {
                return new TableCell<Mail, ArrayList<String>>() {
                    @Override
                    protected void updateItem(ArrayList<String> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.get(0));
                        }
                    }
                };
            }
        });
        bodyInboxColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Mail, String> call(TableColumn<Mail, String> column) {
                return new TableCell<Mail, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.replaceAll("\\<.*?>",""));
                        }
                    }
                };
            }
        });
        inboxTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2){
                    Mail selectedMail = (Mail) inboxTable.getSelectionModel().getSelectedItem();
                    if(selectedMail!=null){
                        try {
                            openMail(selectedMail);
                        } catch (IOException e) {
                            showError("There has been an error opening the mail: "+e.getMessage());
                        }
                    }
                }
            }
        });

        sessionManager.setMailBox(mailBox);

        inboxTable.getItems().addAll(mailBox.getReceived());
    }
    private void fullfillSent(){

        fromSentColumn.setCellValueFactory(new PropertyValueFactory<>("From"));
        subjectSentColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        bodySentColumn.setCellValueFactory(new PropertyValueFactory<>("body"));
        fromSentColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Mail, ArrayList<String>> call(TableColumn<Mail, ArrayList<String>> column) {
                return new TableCell<Mail, ArrayList<String>>() {
                    @Override
                    protected void updateItem(ArrayList<String> item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.get(0));
                        }
                    }
                };
            }
        });
        bodySentColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Mail, String> call(TableColumn<Mail, String> column) {
                return new TableCell<Mail, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.replaceAll("\\<.*?>",""));
                        }
                    }
                };
            }
        });
        sentTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2){
                    Mail selectedMail = (Mail) sentTable.getSelectionModel().getSelectedItem();
                    if(selectedMail!=null){
                        try {
                            openMail(selectedMail);
                        } catch (IOException e) {
                            showError("There has been an error opening the mail: "+e.getMessage());
                        }
                    }
                }
            }
        });

        sentTable.getItems().addAll(mailBox.getSent());
    }

    public void welcomeBag(Bag bag){
        mailBox=(MailBox) bag.getPayload();
        sessionManager.setMailBox(mailBox);
        fullfillInbox();
        fullfillSent();
    }

    public void openMail(Mail mail) throws IOException {

        OpenMessageController openMessageController = new OpenMessageController();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("open-message-view.fxml"));
        fxmlLoader.setController(openMessageController);
        openMessageController.setSessionManager(sessionManager);

        Scene scene = new Scene(fxmlLoader.load());
        Stage secondStage = new Stage();
        secondStage.setTitle("Hello!");
        secondStage.setScene(scene);
        secondStage.show();

        openMessageController.setMail(mail);
    }

    public void openNewMail() throws IOException{
        NewMessageController newMessageController = new NewMessageController();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("new-view.fxml"));
        fxmlLoader.setController(newMessageController);
        newMessageController.setSessionManager(sessionManager);

        Scene scene = new Scene(fxmlLoader.load());
        Stage secondStage = new Stage();
        secondStage.setTitle("New message");
        secondStage.setScene(scene);
        secondStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        RefreshMailbox refreshService = new RefreshMailbox(sessionManager, inboxTable, sentTable);
        refreshService.setPeriod(Duration.seconds(30)); // The interval between executions.
        refreshService.start();

    }
}