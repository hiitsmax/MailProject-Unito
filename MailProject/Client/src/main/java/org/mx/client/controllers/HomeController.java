package org.mx.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.mx.client.services.SessionManager;
import org.mx.post.center.MailBox;
import org.mx.post.entities.Bag;
import org.mx.post.entities.Mail;

import java.util.ArrayList;

public class HomeController {
    @FXML
    private TextField mailField;
    @FXML
    private TextField passwordField;
    @FXML
    private TableView inboxTable;
    @FXML
    private TableColumn<Mail, ArrayList<String>> fromInboxColumn;
    @FXML
    private TableColumn<Mail, String> bodyInboxColumn;

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

    public void welcomeBag(Bag bag){
        mailBox=(MailBox) bag.getPayload();
        fromInboxColumn.setCellValueFactory(new PropertyValueFactory<>("From"));
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

        inboxTable.getItems().addAll(mailBox.getReceived());
    }
}