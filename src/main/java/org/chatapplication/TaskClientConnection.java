package org.chatapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;


public class TaskClientConnection implements Runnable {

    Socket socket;
    Server server;
    String nameOfClient;
    // Create data input and output streams
    DataInputStream input;
    DataOutputStream output;

    public TaskClientConnection(Socket socket, Server server, DataInputStream input, DataOutputStream output) {
        this.socket = socket;
        this.server = server;
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        try {
            // Create data input and output streams
            String name = input.readUTF();
            nameOfClient = name;
            while (true) {
                // Get message from the client

                String message = input.readUTF();
                String content = message.split("\n")[0];
                String sender = message.split("\n")[1];
                String recipient = message.split("\n")[2];
                String token = message.split("\n")[3];
                if (validToken(sender, token)) {
                    updateChatHistory(content, sender, recipient);
                    server.broadcast(message);

                    if (message.contains("exit")) {
                        server.broadcastOnlineUser(sender + " has left the chat");
                        server.txtAreaDisplay.appendText(sender + " offline at " + new Date() + "\n");
                        server.connectionList.remove(this);
                        socket.close();
                        break;
                    }
                }
            }

        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateChatHistory(String content, String sender, String receiver) throws SQLException {
        Connection connection = DataSource.getConnection();
        String sql = "INSERT INTO chat_history (content, sender, receiver) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, content);
        statement.setString(2, sender);
        statement.setString(3, receiver);
        statement.executeUpdate();
        connection.close();
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

    public boolean validToken(String sender, String token) throws SQLException {
        Connection connection = DataSource.getConnection();
        String sql = "SELECT * FROM access_token WHERE user_name = ? AND token = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, sender);
        statement.setString(2, token);
        return statement.executeQuery().next();
    }
}
