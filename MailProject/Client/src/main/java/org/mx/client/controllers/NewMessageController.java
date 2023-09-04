package org.mx.client.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import org.mx.client.services.SessionManager;
import org.mx.post.center.MailBox;
import org.mx.post.entities.Bag;
import org.mx.post.entities.Mail;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;

public class NewMessageController implements Initializable {
    @FXML
    private TextField toField;
    @FXML
    private TextField ccField;
    @FXML
    private TextField ccnField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextField passwordField;
    @FXML
    private HTMLEditor editHTMLEditor;
    @FXML
    private Button sendButton;
    @FXML
    private TableColumn<Mail, ArrayList<String>> fromInboxColumn;
    @FXML
    private TableColumn<Mail, String> bodyInboxColumn;

    private SessionManager sessionManager;
    private MailBox mailBox;
    private Mail mail;



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

    public void setMail(Mail mail){
        for (String address : mail.getTo()){
            toField.setText(toField.getText()+address+";");
        }
        for (String address : mail.getCC()){
            ccField.setText(ccField.getText()+address+";");
        }
        for (String address : mail.getCCn()){
            ccnField.setText(ccnField.getText()+address+";");
        }
        editHTMLEditor.setHtmlText(mail.getBody());
    }

    @FXML
    public void onSendButtonClick(){
        if(checkReceivers()){
            sendMail();
        }else{
            showAlert("You are sending the mail to no one.");
        }
    }

    private void cleanMailArray(ArrayList<String> mailList){
        for(int i=0; i<mailList.size(); i++){
            if(mailList.get(i).isEmpty() || mailList.get(i).equals(" ")){
                mailList.remove(i);
            }
        }
    }

    private void cleanMailAddresses(Mail mailToClean){
        cleanMailArray(mailToClean.getTo());
        cleanMailArray(mailToClean.getCCn());
        cleanMailArray(mailToClean.getCC());
    }

    public void sendMail(){
        ArrayList<String> to =  new ArrayList<>(Arrays.asList(toField.getText().split(";")));
        ArrayList<String> cc = new ArrayList<>(Arrays.asList(ccField.getText().split(";")));
        ArrayList<String> ccn = new ArrayList<>(Arrays.asList(ccnField.getText().split(";")));

        mail.setCC(cc);
        mail.setCCn(ccn);
        mail.setTo(to);
        mail.setFrom(new ArrayList<String>(Collections.singletonList(sessionManager.getAccount().getEmail())));
        mail.setBody(editHTMLEditor.getHtmlText());
        mail.setSubject(subjectField.getText());

        cleanMailAddresses(mail);

        Task<Bag> sendTask = new Task<Bag>() {
            @Override
            protected Bag call() throws Exception {
                return sessionManager.sendMail(mail);
            }
        };
        sendTask.setOnSucceeded((e)->{
            if(sendTask.getValue().getResultCode()== Bag.resultCode.SUCCESS){
                onMailSent();
            }else{
                showError(sendTask.getValue().getPayload().toString());
            }
        });
        sendTask.setOnCancelled((e)->{
            showError(sendTask.getException().getMessage());
        });

        Thread sendThread = new Thread(sendTask);
        sendThread.start();
    }

    private void onMailSent(){
        ((Stage)editHTMLEditor.getScene().getWindow()).close();
    }


    public boolean checkReceivers(){
        return !(toField.getText().isBlank()&&ccField.getText().isBlank()&&ccnField.getText().isBlank());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mail = new Mail();
    }
}