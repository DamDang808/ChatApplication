package org.chatapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;


public class TaskClientConnection implements Runnable {

    Socket socket;
    Server server;
    String nameOfClient;
    // Create data input and output streams
    DataInputStream input;
    DataOutputStream output;

    public TaskClientConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            // Create data input and output streams
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            String name = input.readUTF();
            nameOfClient = name;

            while (true) {
                // Get message from the client
                String message = input.readUTF();

                //send message via server broadcast
                server.broadcast(message);

                if (message.equals(nameOfClient + ": exit\n")) {
                    server.broadcast(name + " has left the chat");
                    server.txtAreaDisplay.appendText(name + " offline at " + new Date() + "\n");
                    server.connectionList.remove(this);
                    socket.close();
                    break;
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

    }

    //send message back to client
    public void sendMessage(String message) {
        try {
            output.writeUTF(message);
            output.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
