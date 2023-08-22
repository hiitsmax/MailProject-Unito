package org.mx.post.center;

import org.mx.post.entities.Mail;

import java.util.ArrayList;

public class MailBox  implements java.io.Serializable{
    ArrayList<Mail> sent;
    ArrayList<Mail> received;
    ArrayList<Mail> CCed;
    public MailBox() {
        sent = new ArrayList<Mail>();
        received = new ArrayList<Mail>();
        CCed = new ArrayList<Mail>();
    }
    public MailBox(ArrayList<Mail> sent, ArrayList<Mail> received, ArrayList<Mail> CCed) {
        this.sent = sent;
        this.received = received;
        this.CCed = CCed;
    }

    public ArrayList<Mail> getSent() {
        return sent;
    }

    public void setSent(ArrayList<Mail> sent) {
        this.sent = sent;
    }

    public ArrayList<Mail> getReceived() {
        return received;
    }

    public void setReceived(ArrayList<Mail> received) {
        this.received = received;
    }

    public ArrayList<Mail> getCCed() {
        return CCed;
    }

    public void setCCed(ArrayList<Mail> CCed) {
        this.CCed = CCed;
    }
}
