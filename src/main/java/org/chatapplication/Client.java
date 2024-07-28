package org.chatapplication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Client {
    private int id;
    private String name;
    Socket socket;
    private DataOutputStream output;
    private ClientController clientController;

    public Client(int id, String name, ClientController clientController) {
        this.id = id;
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
        output.writeUTF(name + " " + id);

        //create a thread in order to read message from server continuously
        TaskReadThread task = new TaskReadThread(socket, clientController);
        Thread thread = new Thread(task);
        thread.start();
    }

    public void sendMessage(String message) {
        try {
            String content = message.split("\n")[0];
            Connection connection = DataSource.getConnection();
            String sql = "INSERT INTO chat_history (content, sender_id, receiver) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, content);
            statement.setInt(2, id);
            statement.setString(3, clientController.getRecipient());
            statement.executeUpdate();

            output.writeUTF(message);
            output.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

}