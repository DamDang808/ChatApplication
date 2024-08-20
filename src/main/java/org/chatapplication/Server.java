package org.chatapplication;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Date;
import java.util.Vector;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import javafx.application.Application;
import javafx.application.Platform;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Server extends Application {
    public TextArea txtAreaDisplay;
    Vector<TaskClientConnection> connectionList = new Vector<>();

    @Override
    public void start(Stage primaryStage) {
        // Text area for displaying contents
        txtAreaDisplay = new TextArea();
        txtAreaDisplay.setEditable(false);

        ScrollPane scrollPane = new ScrollPane();   //pane to display text messages
        scrollPane.setContent(txtAreaDisplay);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        // Create a scene and place it in the stage
        Scene scene = new Scene(scrollPane, 450, 500);

        primaryStage.setOnHiding(event -> System.exit(0));
        primaryStage.setTitle("Server: JavaFx Text Chat App"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage


        //create a new thread
        new Thread(() -> {
            try {
                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(ConnectionUtil.port);

                //append message of the Text Area of UI (GUI Thread)
                Platform.runLater(() -> txtAreaDisplay.appendText("New server started at " + new Date() + '\n'));

                //continuous loop
                while (true) {
                    // Listen for a connection request, add new connection to the list
                    Socket socket = serverSocket.accept();
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                    if (checkValidLogin(input, output)) {
                        TaskClientConnection connection = new TaskClientConnection(socket, this, input, output);
                        connectionList.add(connection);

                        Platform.runLater(() -> txtAreaDisplay.appendText("New client connected at " + new Date() + '\n'));
                        //create a new thread
                        Thread thread = new Thread(connection);
                        thread.start();
                    }
                }
            } catch (IOException ex) {
                txtAreaDisplay.appendText(ex.toString() + '\n');
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).start();

        // get all online clients
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    StringBuilder onlineUsers = new StringBuilder("online: ");
                    for (TaskClientConnection clientConnection : this.connectionList) {
                        onlineUsers.append(clientConnection.nameOfClient).append(", ");
                    }
                    broadcastOnlineUser(onlineUsers.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static void main(String[] args) {
        launch(args);
    }

    //send message to all connected clients
    public void broadcast(String message) {
        String content = message.split("\n")[0];
        String sender = message.split("\n")[1];
        String recipient = message.split("\n")[2];
        for (TaskClientConnection clientConnection : connectionList) {
            if (recipient.equals(" ") || recipient.equals(clientConnection.nameOfClient) || sender.equals(clientConnection.nameOfClient)) {
                clientConnection.sendMessage(content);
            }
        }

    }

    // update online users list to all connected clients
    public void broadcastOnlineUser(String message) {
        for (TaskClientConnection clientConnection : connectionList) {
            clientConnection.sendMessage(message);
        }
    }

    public boolean checkValidLogin(DataInputStream input, DataOutputStream output) throws SQLException, IOException {
        String[] login = input.readUTF().split(" ");

        // Connect to database
        Connection connection = DataSource.getConnection();
        // Check username and password
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE Username = ? AND Password = ?");
        preparedStatement.setString(1, login[0]);
        preparedStatement.setString(2, login[1]);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            String token = generateNewToken(login[0]);
            // insert token into database if token isn't already there else update the token
            PreparedStatement updateStatement = connection.prepareStatement(
                    "INSERT INTO access_token (user_name, token) VALUES (?, ?) ON DUPLICATE KEY UPDATE user_name = ?, token = ?");
            updateStatement.setString(1, login[0]);
            updateStatement.setString(2, token);
            updateStatement.setString(3, login[0]);
            updateStatement.setString(4, token);
            updateStatement.executeUpdate();
            output.writeUTF("valid");
            output.writeUTF(token);
        } else {
            output.writeUTF("Login failed");
            return false;
        }
        connection.close();
        return true;
    }

    public String generateNewToken(String username) throws IOException {
        String original = username + new Date();
        return Hashing.sha256()
                .hashString(original, StandardCharsets.UTF_8)
                .toString();
    }

    public static void getAllPastMessages(ClientController controller, String clientName) throws SQLException {
        Connection connection = DataSource.getConnection();

        String sql = "SELECT sender, receiver, content FROM chat_history where sender = ? or receiver = ? or receiver = ' '";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, clientName);
        statement.setString(2, clientName);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            String message = rs.getString("content");
            Platform.runLater(() -> controller.chatArea.appendText(message + "\n"));
        }
        connection.close();
    }
}