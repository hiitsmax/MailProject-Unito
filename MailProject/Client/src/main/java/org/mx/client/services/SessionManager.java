package org.mx.client.services;

import javafx.collections.ObservableList;
import org.mx.post.center.MailBox;
import org.mx.post.entities.Account;
import org.mx.post.entities.Bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SessionManager {
    private ObservableList<String> logList;
    private int port=6969;

    public Bag<MailBox> handleLogin(String mail, String password) throws Exception {
        Account account = new Account(mail, password);
                try(Socket socket = new Socket("localhost", port)){
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                    Bag<Account> credentialBag = new Bag<>(account, Bag.bagType.LOGIN_CREDENTIALS, null);
                        objectOutputStream.writeObject(credentialBag);
                        objectOutputStream.flush();
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                    if(objectInputStream.readObject() instanceof Bag responseBag)
                        return responseBag;
                    else
                        throw new ClassCastException("Got a non bag");

                }catch (IOException | ClassNotFoundException e){
                    throw new Exception("Communication error", e);
                }
            }


}
