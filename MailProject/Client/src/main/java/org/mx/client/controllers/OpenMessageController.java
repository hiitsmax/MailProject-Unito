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

import java.io.IOException;
import java.util.*;

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
    @FXML
    private Button forwardButton;

    private SessionManager sessionManager;
    private MailBox mailBox;
    private Mail thisMail;

    @FXML
    private void onForwardClick(){
        Mail forwardMail = new Mail();

        forwardMail.setTo(new ArrayList<String>());
        forwardMail.setCC(new ArrayList<String>());
        forwardMail.setCCn(new ArrayList<String>());
        forwardMail.setSubject(thisMail.getSubject());

        String body="</br></br><hr><h3>Forwarded message</h3></h3>";
        body+=getFormattedMail(thisMail,0);
        body+="</br></br>";
        body+=getThreadHTML(thisMail);

        forwardMail.setBody(body);

        try {
            openNewMail(forwardMail);
        } catch (IOException e) {
            showError("There has been an error opening the mail: "+e.getMessage());
        }
    }

    @FXML
    private void onDeleteClick(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Mail");
        alert.setHeaderText("Are you sure want to delete this mail?");

        // option != null.
        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == ButtonType.OK) {

            Stage thisStage = (Stage)forwardButton.getScene().getWindow();
            thisStage.close();

            try {
                sessionManager.deleteMail(thisMail);
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }

    }

    @FXML
    private void onReplyClick(){
        Mail replyMail = new Mail();

        ArrayList<String> to = new ArrayList<>();

        to.addAll(thisMail.getFrom());

        replyMail.setTo(to);
        replyMail.setFrom(new ArrayList<String>(Arrays.asList(new String[]{sessionManager.getAccount().getEmail()})));
        replyMail.setCCn(new ArrayList<String>());
        replyMail.setThreadUUID(thisMail.getThreadUUID());
        replyMail.setThreadStarter(false);
        replyMail.setLastMailUUID(thisMail.getUUID());
        replyMail.setSubject(thisMail.getSubject());

        String body="</br></br>";
        body+=getThreadBoxHTML(thisMail, false, true);

        replyMail.setBody(body);

        try {
            openNewMail(replyMail);
        } catch (IOException e) {
            showError("There has been an error opening the mail: "+e.getMessage());
        }
    }
    @FXML
    private void onReplyAllClick(){
        Mail replyMail = new Mail();

        ArrayList<String> to = new ArrayList<>();
        ArrayList<String> cc = new ArrayList<>();

        to.addAll(thisMail.getFrom());
        for(String mailToAdd :  thisMail.getTo()){
            if(!(mailToAdd.equals(sessionManager.getAccount().getEmail()))) {
                to.add(mailToAdd);
            }
        }

        for(String mailToAdd :  thisMail.getCC()){
            if(!(mailToAdd.equals(sessionManager.getAccount().getEmail()))) {
                cc.add(mailToAdd);
            }
        }

        replyMail.setTo(to);
        replyMail.setCC(cc);
        replyMail.setCCn(new ArrayList<String>());
        replyMail.setFrom(new ArrayList<String>(Arrays.asList(new String[]{sessionManager.getAccount().getEmail()})));
        replyMail.setThreadUUID(thisMail.getThreadUUID());
        replyMail.setThreadStarter(false);
        replyMail.setLastMailUUID(thisMail.getUUID());
        replyMail.setSubject(thisMail.getSubject());

        String body="</br></br>";
        body+=getThreadBoxHTML(thisMail, false, true);

        replyMail.setBody(body);

        try {
            openNewMail(replyMail);
        } catch (IOException e) {
            showError("There has been an error opening the mail: "+e.getMessage());
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

    public void setMail(Mail mail){
        this.thisMail=mail;
        mailBox=sessionManager.getMailBox();
        String body="";

        body+=getFormattedMail(mail,0);
        if(!(mailBox.getMailThread(mail, false).isEmpty())){
            body+="<br><hr><h3>Thread</h3><br>";
            body+=getThreadHTML(mail);
        }

        body=body.replace("contenteditable=\"true\"", "contenteditable=\"false\"");

        System.out.println("\n");
        System.out.println(body);
        System.out.println("\n");
        messageWebView.getEngine().loadContent(body);
    }

    private String getFormattedMail(Mail mailToFormat, int marginPixel){
        String formattedHTML = "<div style=\"margin-left:"+marginPixel+"px\">";

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
        formattedHTML+="</br>";

        formattedHTML+="<b>Subject: </b>";
        formattedHTML+=mailToFormat.getSubject();
        formattedHTML+="</br>";

        formattedHTML+="<b>Datetime: </b>";
        formattedHTML+=mailToFormat.getSentDate().toString();
        formattedHTML+="</br>";

        // DEBUGG
        /*formattedHTML+="<b>UUID: </b>";
        formattedHTML+=mailToFormat.getUUID().toString();
        formattedHTML+="</br>";

        formattedHTML+="<b>Thread-UUID: </b>";
        formattedHTML+=mailToFormat.getThreadUUID().toString();
        formattedHTML+="</br>";

        formattedHTML+="<b>Last-Mail-UUID: </b>";
        formattedHTML+=mailToFormat.getLastMailUUID().toString();
        formattedHTML+="</br>";*/

        formattedHTML+="</br></br>";

        formattedHTML+=mailToFormat.getBody();

        formattedHTML+="</div>";


        return formattedHTML;
    }

    private String getThreadBoxHTML(Mail mailToFormat, boolean isExpanded, boolean includeThis){
        String body="";
        Boolean showBox=false;
        ArrayList<Mail> mailTrace = new ArrayList<>();
        for(Mail singleMail : mailBox.getMailThread(thisMail, includeThis)){
            if(!(mailTrace.contains(singleMail)) && singleMail.getSentDate().compareTo(mailToFormat.getSentDate())<=0){
                showBox=true;
                body+=getFormattedMail(singleMail,0);
                body+="</br><hr></br>";
                mailTrace.add(singleMail);
            }
        }
        if(showBox){
            String uniqueUUID = UUID.randomUUID().toString().replace("-","");
            uniqueUUID+=mailToFormat.getUUID().replace("-","");
            body+="<div>";
            body += "<script>function press"+uniqueUUID+"(){" +
                "if(document.getElementById(\"threadContainer"+uniqueUUID+"\").style.display===\"none\"){" +
                "document.getElementById(\"threadContainer"+uniqueUUID+"\").style.display=\"block\"}" +
                "else{" +
                "document.getElementById(\"threadContainer"+uniqueUUID+"\").style.display=\"none\"}" +
                "}" +
                "</script>";
            String boxString = "<div onclick= \"press"+uniqueUUID+"()\" style=\"user-select:none; width:2em; height:1em; border: solid 1px black; border-radius:20px; background-color: grey;" +
                    "display:flex; align-items:center; justify-content:center; cursor:pointer;\"><span style=\"color:white\">...</span></div>";
            body = boxString + "<div style=\"display:"+(isExpanded?"block":"none")+"; margin-left:8px;\" id=\"threadContainer"+uniqueUUID+"\" >" + body + "</div>";
            body+="</div>";
        }
        return body;
    }

    private String getThreadHTML(Mail mailToFormat){

        String body="";
        Boolean showBox=false;
        ArrayList<Mail> mailTrace = new ArrayList<>();

        for(Mail singleMail : mailBox.getMailThread(thisMail, false)){
            if(!Objects.equals(singleMail.getUUID(), mailToFormat.getUUID()) && !(mailTrace.contains(singleMail))){
                showBox=true;
                body+=getFormattedMail(singleMail, 16);
                body+="</br><hr></br>";
                mailTrace.add(singleMail);
            }
        }

        return body;
    }


    public void openNewMail(Mail mailToStart) throws IOException {
        NewMessageController newMessageController = new NewMessageController();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("new-view.fxml"));
        fxmlLoader.setController(newMessageController);
        newMessageController.setSessionManager(sessionManager);

        Scene scene = new Scene(fxmlLoader.load());
        Stage secondStage = new Stage();
        secondStage.setTitle("New message");
        secondStage.setScene(scene);
        secondStage.show();

        newMessageController.setMail(mailToStart);
    }


}