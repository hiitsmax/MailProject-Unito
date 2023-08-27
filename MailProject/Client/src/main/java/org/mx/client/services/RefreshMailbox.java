package org.mx.client.services;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import org.mx.post.center.MailBox;
import org.mx.post.entities.Mail;

import java.util.ArrayList;

public class RefreshMailbox extends ScheduledService<Void> {

    SessionManager sessionManager;
    TableView inboxTable;
    TableView sentTable;

    public RefreshMailbox(SessionManager sessionManager, TableView inboxTable, TableView sentTable){
        this.sessionManager=sessionManager;
        this.sentTable=sentTable;
        this.inboxTable=inboxTable;
    }

    @Override
    protected Task<Void> createTask(){
        return new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                // here i should update the main mailbox, get the difference, spam notifications and refresh the whole thing
                MailBox difference = sessionManager.refreshMailbox();
                ArrayList<Mail> mailsToNotify = new ArrayList();

                mailsToNotify.addAll(difference.getCCed());
                mailsToNotify.addAll(difference.getReceived());

                inboxTable.getItems().addAll(mailsToNotify);
                sentTable.getItems().addAll(difference.getSent());

                Platform.runLater(() -> {
                    for(Mail mail : mailsToNotify){
                        //Here we should notify the whole thing
                        System.out.println("New maill: " + mail.getBody());
                    }
                });
                return null;
            }
        };
    }
}