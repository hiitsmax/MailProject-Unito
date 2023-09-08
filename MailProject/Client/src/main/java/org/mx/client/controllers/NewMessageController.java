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
import java.util.regex.*;

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

    public void setMail(Mail mailToSet){
        for (String address : mailToSet.getTo()){
            toField.setText(toField.getText()+address+";");
        }
        for (String address : mailToSet.getCC()){
            ccField.setText(ccField.getText()+address+";");
        }
        for (String address : mailToSet.getCCn()){
            ccnField.setText(ccnField.getText()+address+";");
        }
        mail.setUUID(mailToSet.getUUID());
        mail.setThreadUUID(mailToSet.getThreadUUID());
        mail.setThreadStarter(mailToSet.getThreadStarter());
        mail.setLastMailUUID(mailToSet.getLastMailUUID());
        subjectField.setText(mailToSet.getSubject());
        editHTMLEditor.setHtmlText(mailToSet.getBody());
    }

    @FXML
    public void onSendButtonClick(){
        if(checkAndPutReceivers()){
            sendMail();
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
        mail.setFrom(new ArrayList<String>(Collections.singletonList(sessionManager.getAccount().getEmail())));
        mail.setBody(editHTMLEditor.getHtmlText());
        mail.setSubject(subjectField.getText());

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


    public boolean checkAndPutReceivers(){
        ArrayList<String> to =  new ArrayList<>(Arrays.asList(toField.getText().split(";")));
        ArrayList<String> cc = new ArrayList<>(Arrays.asList(ccField.getText().split(";")));
        ArrayList<String> ccn = new ArrayList<>(Arrays.asList(ccnField.getText().split(";")));

        toField.setText(toField.getText().replace(" ",""));
        ccField.setText(ccField.getText().replace(" ",""));
        ccnField.setText(ccnField.getText().replace(" ",""));

        mail.setCC(cc);
        mail.setCCn(ccn);
        mail.setTo(to);

        for(String toS : to )System.out.println("TOS "+toS);

        boolean isOK=true;
        cleanMailAddresses(mail);


        ArrayList<String> wrongMails = new ArrayList<>();
        ArrayList<String> allMails = new ArrayList<>();
        allMails.addAll(to); allMails.addAll(cc); allMails.addAll(ccn);

        Pattern mailValidator = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        for(String mailToCheck : allMails){
            System.out.println("CHEK MAIL "+mailToCheck);
            if(!(mailValidator.matcher(mailToCheck).matches())){
                wrongMails.add(mailToCheck);
                System.out.println("WRONG MAIL "+mailToCheck);
            }
        }

        StringBuilder message = new StringBuilder();

        if(to.isEmpty()&&cc.isEmpty()&&ccn.isEmpty()){
            message.append("Insert at least one email address");
                    isOK=false;
        }
        if(!wrongMails.isEmpty()) {
            message.append("Check the following addresses:\n");
            for(String wrongMail : wrongMails)
                message.append(wrongMail).append("\n");
            isOK = false;
        }

        if(!isOK){
            showError(message.toString());
        }

        return isOK;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mail = new Mail();
    }
}