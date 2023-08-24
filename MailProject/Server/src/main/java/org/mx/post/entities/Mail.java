package org.mx.post.entities;

import java.util.ArrayList;
import java.util.Date;

public class Mail implements java.io.Serializable{
    String UUID;
    String threadUUID;
    Boolean isThreadStarter=true;
    String body;
    String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    ArrayList<String> To;
    ArrayList<String> CC;
    ArrayList<String> CCn;
    ArrayList<String> From;
    Date SentDate;

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getThreadUUID() {
        return threadUUID;
    }

    public void setThreadUUID(String threadUUID) {
        this.threadUUID = threadUUID;
    }

    public Boolean getThreadStarter() {
        return isThreadStarter;
    }

    public void setThreadStarter(Boolean threadStarter) {
        isThreadStarter = threadStarter;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ArrayList<String> getTo() {
        return To;
    }

    public void setTo(ArrayList<String> to) {
        To = to;
    }

    public ArrayList<String> getCCn() {
        return CCn;
    }

    public void setCCn(ArrayList<String> CCn) {
        this.CCn = CCn;
    }

    public ArrayList<String> getCC() {
        return CC;
    }

    public void setCC(ArrayList<String> CC) {
        this.CC = CC;
    }

    public ArrayList<String> getFrom() {
        return From;
    }

    public void setFrom(ArrayList<String> from) {
        From = from;
    }

    public Date getSentDate() {
        return SentDate;
    }

    public void setSentDate(Date sentDate) {
        SentDate = sentDate;
    }

    public Mail(String UUID, String threadUUID, Boolean isThreadStarter, String body, ArrayList<String> to, ArrayList<String> CC, ArrayList<String> from, Date sentDate) {
        this.UUID = UUID;
        this.threadUUID = threadUUID;
        this.isThreadStarter = isThreadStarter;
        this.body = body;
        To = to;
        this.CC = CC;
        From = from;
        SentDate = sentDate;
    }

    public Mail(){
        this.To = new ArrayList<>();
        this.CC = new ArrayList<>();
        this.From = new ArrayList<>();
    }
}
