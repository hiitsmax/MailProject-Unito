package org.mx.server.services;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import org.mx.post.center.MailBox;
import org.mx.post.center.PostalCenter;
import org.mx.post.entities.Account;
import org.mx.post.entities.Bag;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SessionManager {
    private Task<Void> serverTask;
    private Thread serverThread;
    private ObservableList<String> logList;
    private PostalCenter postalCenter;
    private int port=6969;

    public void setLogList(ObservableList<String> list){
        logList=list;
    }
    public void setPostalCenter(PostalCenter postalCenter){
        this.postalCenter=postalCenter;
    }
    public void start(){
        serverTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                startServer();
                return null;
            }
        };

        serverThread = new Thread(serverTask);
        serverThread.start();
    }

    public void stop(){
        if(serverThread!=null){
            serverThread.interrupt();
        }
    }

    private void startServer(){
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log("Server socket opened");
            while(!serverThread.isInterrupted()){
                Socket clientSocket = serverSocket.accept();
                log("Accepted connection on "+clientSocket.getInetAddress());
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            showError(e.getMessage());
            log("Error while starting server: "+e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket){
        Task<Void> clientTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try{
                    log("Getting socket streams...");
                    ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                    log("Reading object...");
                    Bag resultBag= (Bag) objectInputStream.readObject();
                    // Seems broken don't know why
                    // if(objectInputStream.readObject() instanceof Bag bag){
                        log("Bag received, type: " + resultBag.getType());
                        log("Bag payload: "+resultBag.getPayload().getClass().getName());
                        switch (resultBag.getType()){
                            case LOGIN_CREDENTIALS -> resultBag=handleLogin(resultBag);
                        }
                    // }
                    log("Object processed");

                    if(resultBag!=null) {
                        log("Sending response bag...");
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                        objectOutputStream.writeObject(resultBag);
                        objectOutputStream.flush();
                        objectOutputStream.close();
                        objectInputStream.close();
                    }else{
                        log("Error processing request: see above");
                    }

                    clientSocket.close();
                }catch (IOException | ClassNotFoundException e){
                    log("Error processing request: "+e.getMessage());
                }

                return null;
            }
        };

        Thread clientThread = new Thread(clientTask);
        clientThread.start();
    }

    private Bag handleLogin(Bag incomingBag){
        log("Handling login...");
        if(incomingBag.getPayload() instanceof Account){
            Account account = (Account) incomingBag.getPayload();
            log("Processing account");
            if(postalCenter.isAccountValid(account)){
                log("Account found!");
                return new Bag<>(postalCenter.getMailBoxOf(account.getEmail()), Bag.bagType.MAILBOX, Bag.resultCode.SUCCESS);
            }else{
                log("Account not found :(");
                return new Bag<>(null, Bag.bagType.LOGIN_CREDENTIALS, Bag.resultCode.FAILED);
            }
        }

        return null;
    }


    private void showError(String messageError){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("An error has happened");
        alert.setContentText(messageError);

        alert.showAndWait();
    }

    private void log(String line){
        Platform.runLater(()->logList.add(line));
    }
}
