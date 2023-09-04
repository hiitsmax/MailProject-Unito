package org.mx.post.center;

import org.mx.post.entities.Account;
import org.mx.post.entities.Mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class PostalCenter implements java.io.Serializable{
    AccountVault accountVault;
    MailVault mailVault;
    public PostalCenter() {
        accountVault = new AccountVault();
        mailVault = new MailVault();

        ArrayList<String> to=new ArrayList<>();
        to.add("hiitsmax@post.com");

        mailVault.addMail(new Mail(
                "AAAA",
                "AAAAA",
                true,
                "porcatroia",
                to,
                new ArrayList<>(),
                to,
                new Date()
        ));
    }

    public boolean isAccountPresent(String mail){
        for(Account account: accountVault.getAccounts()){
            if(account.getEmail().equals(mail)) return true;
        }
        return false;
    }

    public void addMail(Mail mail){
        mail.setSentDate(new Date());
        for(String toAdd : mail.getTo()){
            System.out.println("ADD MAIL IN TO: "+toAdd);
        }
        mailVault.addMail(mail);
    }

    public Account getAccountByEmail(String email){
        for (Account account : accountVault.getAccounts()) {
            if (account.getEmail().equals(email)) {
                return account;
            }
        }
        return null;
    }
    public boolean isAccountValid(Account account){
        return accountVault.getAccounts().contains(account);
    }
    public MailBox getMailBoxOf(String mail){
        Account account = accountVault.getAccountByEmail(mail);
        return new MailBox(mailVault.getMailSentBy(account), mailVault.getMailSentTo(account), mailVault.getMailCCedTo(account));
    }
    public MailBox getMailBoxOf(Account account){
        return new MailBox(mailVault.getMailSentTo(account), mailVault.getMailSentBy(account), mailVault.getMailCCedTo(account));
    }
    public ArrayList<Mail> getMailSentBy(Account account){
        return mailVault.getMailSentBy(account);
    }
    public ArrayList<Mail> getMailSentBy(String mail){
        return mailVault.getMailSentBy(accountVault.getAccountByEmail(mail));
    }


}
