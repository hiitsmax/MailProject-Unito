package org.mx.post.center;

import org.mx.post.entities.Mail;

import java.util.ArrayList;

public class MailBox implements java.io.Serializable{
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

    public MailBox getDifferenceFrom(MailBox mailBox){
        ArrayList<Mail> sentDifference = getDifference(sent, mailBox.getSent());
        ArrayList<Mail> ccedDifference = getDifference(CCed, mailBox.getCCed());
        ArrayList<Mail> receivedDifference = getDifference(received, mailBox.getReceived());

        MailBox resultBox = new MailBox();
        resultBox.setReceived(receivedDifference);
        resultBox.setCCed(ccedDifference);
        resultBox.setSent(sentDifference);

        return resultBox;
    }

    public ArrayList<Mail> getMailThread(Mail mail, Boolean includeThisMail){
        ArrayList<Mail> allMails = new ArrayList<>();
        ArrayList<Mail> mailThread = new ArrayList<>();
        allMails.addAll(sent);
        allMails.addAll(received);
        allMails.addAll(CCed);

        Mail lastMail=mail;
        Boolean found=true;
        while(found){
            found = false;
            for(Mail singleMail: allMails){
                System.out.println("SINGLE MAIL "+singleMail.getLastMailUUID());
                if(singleMail.getUUID().equals(lastMail.getLastMailUUID())){
                    System.out.println("TRUE");
                    mailThread.add(singleMail);
                    lastMail=singleMail;
                    found=true;
                }
            }
        }
            if(includeThisMail)
                mailThread.add(0, mail);



        /*mailThread.sort(new Comparator<Mail>() {
            @Override
            public int compare(Mail a, Mail b) {
                return a.getSentDate().compareTo(b.getSentDate());
            }
        });*/

        return mailThread;
    }

    private ArrayList<Mail> getDifference(ArrayList<Mail> mailListA, ArrayList<Mail> mailListB){
        ArrayList<Mail> difference = new ArrayList<Mail>();
        for(Mail thisMail: mailListA){
            boolean found = false;
            for(Mail otherMail: mailListB){
                if(thisMail.getUUID().equals(otherMail.getUUID())){
                    found=true;
                }
            }
            if(!found){
                difference.add(thisMail);
            }
        }
        return difference;
    }

}
