package org.chatapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    private Client client;
    private String name;

    public ClientApplication(String name) {
        this.name = name;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("client-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 720, 480);

        ClientController controller = fxmlLoader.getController();

        client = new Client(name, controller);
        client.start();

        controller.setClient(client);
        controller.setNameLabel(client.getName());
        controller.setStage(stage);

        stage.setOnHiding(e -> System.exit(0));

        stage.setTitle("Chat Application");
        stage.setScene(scene);
        stage.show();
    }
}