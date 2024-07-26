package org.chatapplication;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Platform;

public class TaskReadThread implements Runnable {
    Socket socket;
    ClientController client;
    DataInputStream input;

    //constructor
    public TaskReadThread(Socket socket, ClientController client) {
        this.socket = socket;
        this.client = client;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Create data input stream
                input = new DataInputStream(socket.getInputStream());

                // get data from the server
                String message = input.readUTF();
                if (message.startsWith("online"))
                    client.updateOnlineUsers(message);
                else {
                    // append message of the Text Area of UI (GUI Thread)
                    Platform.runLater(() -> {
                        //display the message in the textarea
                        client.chatArea.appendText(message + "\n");
                    });
                }
            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }
}
