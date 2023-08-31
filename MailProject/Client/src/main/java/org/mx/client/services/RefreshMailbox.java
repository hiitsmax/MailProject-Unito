package org.mx.client.services;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import org.controlsfx.control.Notifications;
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
                MailBox difference = sessionManager.refreshMailbox();
                ArrayList<Mail> mailsToNotify = new ArrayList();

                mailsToNotify.addAll(difference.getCCed());
                mailsToNotify.addAll(difference.getReceived());

                inboxTable.getItems().addAll(mailsToNotify);
                sentTable.getItems().addAll(difference.getSent());
                System.out.println(mailsToNotify.size());
                System.out.println(difference.getReceived().size());

                for(Mail mail : mailsToNotify){
                    //Here we should notify the whole thing
                    System.out.println(mail);
                    Notifications.create().position(Pos.TOP_CENTER).text(mail.getBody().replaceAll("\\<.*?>","")).title(mail.getFrom().get(0)+mail.getSubject()).showWarning();
                }
                Platform.runLater(() -> {
                    System.out.println("JJJJ");
                });
                return null;
            }
        };
    }
}