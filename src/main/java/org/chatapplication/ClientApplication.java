package org.chatapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientApplication extends Application {
    private Client client;
    private String clientName;
    private String token;

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public ClientApplication(String name, String token, Socket socket, DataInputStream input, DataOutputStream output) {
        this.clientName = name;
        this.token = token;
        this.socket = socket;
        this.input = input;
        this.output = output;
    }

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("client-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 720, 480);

        ClientController controller = fxmlLoader.getController();

        client = new Client(clientName, token, controller, socket, input, output);
        client.start();

        controller.setClient(client);
        controller.setNameLabel(client.getName());
        controller.setStage(stage);
        Server.getAllPastMessages(controller, clientName);

        stage.setTitle("Chat Application");
        stage.setScene(scene);
        stage.show();
    }
}