package org.chatapplication;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class ClientApplication extends Application {
    private Client client;
    private int clientId;
    private String clientName;

    public ClientApplication(int id, String name) {
        this.clientId = id;
        this.clientName = name;
    }

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("client-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 720, 480);

        ClientController controller = fxmlLoader.getController();

        client = new Client(clientId, clientName, controller);
        client.start();

        controller.setClient(client);
        controller.setNameLabel(client.getName());
        controller.setStage(stage);
        getAllPastMessages(controller);

        stage.setTitle("Chat Application");
        stage.setScene(scene);
        stage.show();
    }

    public void getAllPastMessages(ClientController controller) throws SQLException {
        Connection messages = DataSource.getConnection();

        String sql = "SELECT content FROM chat_history";
        PreparedStatement statement = messages.prepareStatement(sql);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            String message = rs.getString("content");
            Platform.runLater(() -> controller.chatArea.appendText(message));
        }
    }
}