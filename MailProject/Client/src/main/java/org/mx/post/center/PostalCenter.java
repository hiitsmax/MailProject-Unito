package org.mx.post.center;

import org.mx.post.entities.Account;
import org.mx.post.entities.Mail;

import java.util.ArrayList;
import java.util.Date;

public class PostalCenter implements java.io.Serializable{
    AccountVault accountVault;
    MailVault mailVault;
    public PostalCenter() {
        accountVault = new AccountVault();
        mailVault = new MailVault();

        ArrayList<String> to=new ArrayList<>();
        ArrayList<String> from=new ArrayList<>();

        mailVault.addMail(new Mail(
                "AAAA",
                "AAAAA",
                true,
                "porcatroia",
                to,
                new ArrayList<>(),
                from,
                new Date()
        ));
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
        return new MailBox(mailVault.getMailSentTo(account), mailVault.getMailSentBy(account), mailVault.getMailCCedTo(account));
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
