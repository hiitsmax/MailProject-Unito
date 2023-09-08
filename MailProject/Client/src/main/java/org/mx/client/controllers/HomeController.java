package org.mx.client.controllers;

import javafx.beans.property.SimpleStringProperty;
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
import java.util.*;

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
    @FXML
    private TableColumn<Mail, Date> timestampInboxColumn;
    @FXML
    private TableColumn<Mail, Date> timestampSentColumn;
    @FXML
    private Label statusLabel;
    @FXML
    private Button deleteMessageButton;
    private SimpleStringProperty status;
    private SessionManager sessionManager;
    private MailBox mailBox;
    private Mail selectedMail;


    @FXML
    private void onDeleteMessageClick(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Mail");
        alert.setHeaderText("Are you sure want to delete this mail?");

        // option != null.
        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == ButtonType.OK) {
            try {
                sessionManager.deleteMail(selectedMail);
                deleteMessageButton.setDisable(true);
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
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

    private void fullfillInbox(){

        fromInboxColumn.setCellValueFactory(new PropertyValueFactory<>("From"));
        subjectInboxColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        bodyInboxColumn.setCellValueFactory(new PropertyValueFactory<>("body"));

        for(Mail CCn : mailBox.getCCned()){
            CCn.setCC(new ArrayList<String>(Arrays.asList(sessionManager.getAccount().getEmail())));
        }

        timestampInboxColumn.setCellValueFactory(new PropertyValueFactory<>("SentDate"));
        timestampInboxColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Mail, Date> call(TableColumn<Mail, Date> column) {
                return new TableCell<Mail, Date>() {
                    @Override
                    protected void updateItem(Date item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.toString());
                        }
                    }
                };
            }
        });
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
                selectedMail = (Mail) inboxTable.getSelectionModel().getSelectedItem();
                if(selectedMail!=null){
                    deleteMessageButton.setDisable(false);
                if(event.getClickCount() == 2){
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
        inboxTable.getItems().addAll(mailBox.getCCed());
        inboxTable.getItems().addAll(mailBox.getCCned());
        inboxTable.getItems().sort(Comparator.comparing(Mail::getSentDate).reversed());
    }
    private void fullfillSent(){

        fromSentColumn.setCellValueFactory(new PropertyValueFactory<>("From"));
        subjectSentColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        bodySentColumn.setCellValueFactory(new PropertyValueFactory<>("body"));

        timestampSentColumn.setCellValueFactory(new PropertyValueFactory<>("SentDate"));
        timestampSentColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Mail, Date> call(TableColumn<Mail, Date> column) {
                return new TableCell<Mail, Date>() {
                    @Override
                    protected void updateItem(Date item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.toString());
                        }
                    }
                };
            }
        });
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
                selectedMail = (Mail) sentTable.getSelectionModel().getSelectedItem();
                if(selectedMail!=null){
                    deleteMessageButton.setDisable(false);
                if(event.getClickCount() == 2){
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
        sentTable.getItems().sort(Comparator.comparing(Mail::getSentDate).reversed());
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

        status = new SimpleStringProperty();

        RefreshMailbox refreshService = new RefreshMailbox(sessionManager, inboxTable, sentTable, status);
        refreshService.setPeriod(Duration.seconds(1)); // The interval between executions.
        refreshService.start();

        statusLabel.textProperty().bind(status);

        fromInboxColumn.maxWidthProperty().bind(inboxTable.widthProperty().divide(4));
        subjectInboxColumn.maxWidthProperty().bind(inboxTable.widthProperty().divide(4));
        timestampInboxColumn.maxWidthProperty().bind(inboxTable.widthProperty().divide(4));
        bodyInboxColumn.maxWidthProperty().bind(inboxTable.widthProperty().divide(4));

        fromSentColumn.maxWidthProperty().bind(inboxTable.widthProperty().divide(4));
        subjectSentColumn.maxWidthProperty().bind(inboxTable.widthProperty().divide(4));
        timestampSentColumn.maxWidthProperty().bind(inboxTable.widthProperty().divide(4));
        bodySentColumn.maxWidthProperty().bind(inboxTable.widthProperty().divide(4));

    }
}