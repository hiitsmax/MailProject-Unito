package org.mx.client.services;

import javafx.collections.ObservableList;
import org.mx.post.center.MailBox;
import org.mx.post.entities.Account;
import org.mx.post.entities.Bag;
import org.mx.post.entities.Mail;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SessionManager {
    private ObservableList<String> logList;
    private Account account;
    private int port = 6969;

    public Account getAccount() {
        return account;
    }

    public Bag sendMail(Mail mail) throws Exception {
        Bag mailBag = new Bag<Mail>(mail, Bag.bagType.MAIL_OUT, Bag.resultCode.SUCCESS);
        return sendBag(mailBag);
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Bag<MailBox> handleLogin(String mail, String password) throws Exception {
        Account account = new Account(mail, password);
        Bag<Account> credentialBag = new Bag<>(account, Bag.bagType.LOGIN_CREDENTIALS, null);

        Bag responseBag = sendBag(credentialBag);

        if (responseBag.getResultCode() == Bag.resultCode.SUCCESS) {
            this.account = account;
        }

        return responseBag;
    }

    public Bag sendBag(Bag bag) throws Exception {
        try (Socket socket = new Socket("localhost", port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(bag);
            objectOutputStream.flush();
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            if (objectInputStream.readObject() instanceof Bag responseBag) {
                return responseBag;
            } else
                throw new ClassCastException("Got a non bag");

        } catch (IOException | ClassNotFoundException e) {
            throw new Exception("Communication error", e);
        }
    }

}
