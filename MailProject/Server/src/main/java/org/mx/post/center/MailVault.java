package org.mx.post.center;

import org.mx.post.entities.Account;
import org.mx.post.entities.Mail;

import java.util.ArrayList;

public class MailVault  implements java.io.Serializable{
    ArrayList<Mail> mails;
    public MailVault() {
        mails = new ArrayList<Mail>();
    }

    public ArrayList<Mail> getMails() {
        return mails;
    }

    public void addMail(Mail mail) {

        mails.add(mail);
    }

    public void removeMail(Mail mail) {
        mails.remove(mail);
    }

    public ArrayList<Mail> getMailSentBy(Account sender){
        ArrayList<Mail> sentBy = new ArrayList<Mail>();
        for (Mail mail : mails) {
            if (mail.getFrom().contains(sender.getEmail()) && mail.shouldIGiveThisMailTo(sender.getEmail())) {
                sentBy.add(mail);
            }
        }
        return sentBy;
    }

    public ArrayList<Mail> getMailSentTo(Account receiver){
        ArrayList<Mail> sentTo = new ArrayList<Mail>();
        for (Mail mail : mails) {
            if (mail.getTo().contains(receiver.getEmail()) && mail.shouldIGiveThisMailTo(receiver.getEmail())) {
                sentTo.add(mail);
            }
        }
        return sentTo;
    }

    public ArrayList<Mail> getMailCCedTo(Account receiver){
        ArrayList<Mail> CCedTo = new ArrayList<Mail>();
        for (Mail mail : mails) {
            if (mail.getCC().contains(receiver.getEmail()) && mail.shouldIGiveThisMailTo(receiver.getEmail())) {
                CCedTo.add(mail);
            }
        }
        return CCedTo;
    }

    public ArrayList<Mail> getMailCCnedTo(Account receiver){
        ArrayList<Mail> CCnedTo = new ArrayList<Mail>();
        for (Mail mail : mails) {
            if (mail.getCCn().contains(receiver.getEmail()) && mail.shouldIGiveThisMailTo(receiver.getEmail())) {
                CCnedTo.add(mail);
            }
        }
        return CCnedTo;
    }

    public ArrayList<Mail> getInbox(Account receiver){
        ArrayList<Mail> inbox = new ArrayList<Mail>();
        inbox.addAll(getMailSentTo(receiver));
        inbox.addAll(getMailCCedTo(receiver));
        return inbox;
    }

    public ArrayList<Mail> getThread(String threadUUID){
        ArrayList<Mail> thread = new ArrayList<Mail>();
        for (Mail mail : mails) {
            if (mail.getThreadUUID().equals(threadUUID)) {
                thread.add(mail);
            }
        }
        return thread;
    }
}
