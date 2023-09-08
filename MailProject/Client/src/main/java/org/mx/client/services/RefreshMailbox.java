package org.mx.client.services;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import org.controlsfx.control.Notifications;
import org.mx.post.center.MailBox;
import org.mx.post.entities.Mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class RefreshMailbox extends ScheduledService<Void> {

    SessionManager sessionManager;
    TableView inboxTable;
    TableView sentTable;
    SimpleStringProperty statusLabel;

    public RefreshMailbox(SessionManager sessionManager, TableView inboxTable, TableView sentTable, SimpleStringProperty statusLabel){
        this.sessionManager=sessionManager;
        this.sentTable=sentTable;
        this.inboxTable=inboxTable;
        this.statusLabel=statusLabel;
    }

    @Override
    protected Task<Void> createTask(){
        return new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                MailBox oldMailBox = sessionManager.getMailBox();
                try{
                    MailBox difference = sessionManager.refreshMailbox();
                    MailBox newMailBox = sessionManager.getMailBox();
                    ArrayList<Mail> mailsToNotify = new ArrayList<Mail>();


                    MailBox mailsToRemove = oldMailBox.getDifferenceFrom(newMailBox);
                    for(Mail a: mailsToRemove.getReceived()) System.out.println(a.getUUID());
                    for(Mail mailToRemove : mailsToRemove.getCCed()){
                        inboxTable.getItems().remove(mailToRemove);
                    }
                    for(Mail mailToRemove : mailsToRemove.getCCned()){
                        inboxTable.getItems().remove(mailToRemove);
                    }
                    for(Mail mailToRemove : mailsToRemove.getReceived()){
                        inboxTable.getItems().remove(mailToRemove);
                    }

                    for(Mail CCn : difference.getCCned()){
                        CCn.setCC(new ArrayList<String>(Arrays.asList(sessionManager.getAccount().getEmail())));
                    }
                    for(Mail mailToRemove : mailsToRemove.getSent()){
                        sentTable.getItems().remove(mailToRemove);
                    }

                    mailsToNotify.addAll(difference.getCCed());
                    mailsToNotify.addAll(difference.getReceived());
                    mailsToNotify.addAll(difference.getCCned());

                    inboxTable.getItems().addAll(mailsToNotify);
                    sentTable.getItems().addAll(difference.getSent());

                    inboxTable.getItems().sort(Comparator.comparing(Mail::getSentDate).reversed());
                    sentTable.getItems().sort(Comparator.comparing(Mail::getSentDate).reversed());

                    Platform.runLater(()->{
                        for(Mail mail : mailsToNotify){
                            String notificationBody = mail.getBody().replaceAll("\\<.*?>","");
                            if(notificationBody.length()>24){
                                notificationBody=notificationBody.substring(0,24) + "...";
                            }
                            Notifications.create().position(Pos.TOP_CENTER).text(notificationBody).title("New mail: "+mail.getFrom().get(0)+" - "+mail.getSubject()).showInformation();
                        }
                    });
                    Platform.runLater(()->{
                        statusLabel.set("Status: Mailbox refreashed at " + (new Date()).toString());
                    });
                }catch (Exception e){
                    Platform.runLater(()->{
                        statusLabel.set("Status: Error refreshing mailbox, "+e.getMessage());
                    });
                }
                return null;
            }
        };
    }
}