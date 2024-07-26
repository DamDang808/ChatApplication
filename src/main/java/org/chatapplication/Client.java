package org.chatapplication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Client {
    private String name;
    Socket socket;
    private DataOutputStream output;
    private ClientController clientController;

    public Client(String name, ClientController clientController) {
        this.name = name;
        this.clientController = clientController;
    }

    public void start() throws IOException {
        // Create a new socket
        socket = new Socket();
        SocketAddress address = new InetSocketAddress(ConnectionUtil.host, ConnectionUtil.port);
        socket.connect(address, 30000);

        // Create an output stream to send data to the server
        output = new DataOutputStream(socket.getOutputStream());
        output.writeUTF(name);

        //create a thread in order to read message from server continuously
        TaskReadThread task = new TaskReadThread(socket, clientController);
        Thread thread = new Thread(task);
        thread.start();
    }

    public void sendMessage(String message) {
        try {
            output.writeUTF(message);
            output.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
}