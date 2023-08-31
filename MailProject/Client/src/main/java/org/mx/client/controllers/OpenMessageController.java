package org.mx.client.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.mx.client.MainApplication;
import org.mx.client.services.SessionManager;
import org.mx.post.center.MailBox;
import org.mx.post.entities.Bag;
import org.mx.post.entities.Mail;

import java.util.ArrayList;

public class OpenMessageController {
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
    @FXML
    private WebView messageWebView;

    private SessionManager sessionManager;
    private MailBox mailBox;
    private Mail thisMail;



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
        this.thisMail=mail;
        mailBox=sessionManager.getMailBox();
        String body="";

        for(Mail singleMail : mailBox.getMailThread(thisMail.getThreadUUID())){
            System.out.println(singleMail.getBody());
            body+=getFormattedMail(singleMail);
        }

        messageWebView.getEngine().loadContent(body);
    }

    private String getFormattedMail(Mail mailToFormat){
        String formattedHTML = "";

        formattedHTML+="<b>From: </b>";
        for(String mail : mailToFormat.getFrom()){
            formattedHTML+=mail+"; ";
        }
        formattedHTML+="</br>";

        formattedHTML+="<b>To: </b>";
        for(String mail : mailToFormat.getTo()){
            formattedHTML+=mail+"; ";
        }
        formattedHTML+="</br>";

        formattedHTML+="<b>CC: </b>";
        for(String mail : mailToFormat.getCC()){
            formattedHTML+=mail+"; ";
        }
        formattedHTML+="</br></br>";

        formattedHTML+= mailToFormat.getBody();
        formattedHTML+="</br><hr></br>";

        return formattedHTML;
    }


}