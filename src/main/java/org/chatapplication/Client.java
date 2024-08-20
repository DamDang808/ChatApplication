package org.chatapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private String name;
    private String token;
    Socket socket;
    private DataOutputStream output;
    private DataInputStream input;
    private ClientController controller;

    public Client(String name, String token, ClientController controller, Socket socket, DataInputStream input, DataOutputStream output) {
        this.name = name;
        this.token = token;
        this.controller = controller;
        this.socket = socket;
        this.input = input;
        this.output = output;
    }

    public void start() {
        //create a thread in order to read message from server continuously
        TaskReadThread task = new TaskReadThread(socket, input, controller);
        Thread thread = new Thread(task);
        thread.start();
    }

    public void sendMessage(String message) {
        try {
            output.writeUTF(message + "\n" + token);
            output.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

}