package org.chatapplication;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Vector;

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
                    TaskClientConnection connection = new TaskClientConnection(socket, this);
                    connectionList.add(connection);

                    Platform.runLater(() -> txtAreaDisplay.appendText("New client connected at " + new Date() + '\n'));
                    //create a new thread
                    Thread thread = new Thread(connection);
                    thread.start();

                }
            } catch (IOException ex) {
                txtAreaDisplay.appendText(ex.toString() + '\n');
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
                    broadcast(onlineUsers.toString());
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
        for (TaskClientConnection clientConnection : this.connectionList) {
            clientConnection.sendMessage(message);
        }
    }
}