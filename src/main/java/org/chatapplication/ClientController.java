package org.chatapplication;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientController {

    @FXML
    public Label nameLabel = new Label();

    @FXML
    public Circle onlineCircle;

    @FXML
    public TextArea chatArea;

    @FXML
    public TextField inputField = new TextField();

    @FXML
    public HBox hBox1;

    @FXML
    public HBox hBox2;

    @FXML
    public VBox vBox1;

    @FXML
    public ListView<String> onlineUsers;

    private Client client;

    private Stage stage;

    private String recipient = " ";



    // Send message when the send button is clicked
    @FXML
    public void setInputFieldOnEnterPressed(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            // send message....
            setOnClickedSendButton();
        }
    }

    @FXML
    public void setOnClickedSendButton() throws IOException {
        // send message....
        StringBuilder message = new StringBuilder();

        if (inputField.getText().startsWith("/p")) {
            recipient = inputField.getText().split(" ")[1];
            inputField.setText("");
            return;
        }
        if (inputField.getText().startsWith("/all")) {
            recipient = " ";
            inputField.setText("");
            return;
        }

        message.append(nameLabel.getText()).append(": ").append(inputField.getText()).append("\n").append(client.getName()).append("\n").append(recipient);
        client.sendMessage(message.toString());
        if (inputField.getText().equals("exit")) {
            stage.close();
            client.socket.close();
        }
        inputField.setText("");
        inputField.requestFocus();
    }


    // Update the list of online users
    public void updateOnlineUsers(String message) {
        String content = message.substring(8);
        if (content.equals("No online users")) {
            onlineUsers.getItems().clear();
            return;
        }
        String[] users = content.split(", ");
        Platform.runLater(() -> {
            onlineUsers.getItems().clear();
            for (String user : users) {
                onlineUsers.getItems().add(user);
            }
        });
    }


    public void setClient(Client client) {
        this.client = client;
    }

    public void setNameLabel(String name) {
        this.nameLabel.setText(name);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public String getRecipient() {
        return recipient;
    }
}