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
import javafx.stage.Window;

import java.io.IOException;

public class ClientController {
    private Stage stage;

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

    private String friendName;

    // Send message when the send button is clicked
    @FXML
    public void setInputFieldOnEnterPressed(KeyEvent keyEvent) throws IOException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            // send message....
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("content", inputField.getText());
//            jsonObject.put("sender", client.getName());
//            jsonObject.put("receiver", friendName);
//
//            client.sendMessage(jsonObject.toJSONString());
            client.sendMessage(client.getName() + ": " + inputField.getText());
            if (inputField.getText().equals("exit")) {
                stage.close();
            }
            inputField.setText("");
        }
    }

    // Update the list of online users
    public void updateOnlineUsers(String message) {
        String[] users = message.substring(7).split(",");
        Platform.runLater(() -> onlineUsers.getItems().clear());
        for (String user : users) {
            Platform.runLater(() -> onlineUsers.getItems().add(user));
        }
    }

    // Choose a user to chat with
    @FXML
    public void setOnClickedListOfOnlineUser() {
//        Platform.runLater(() -> {
//            String selectedUser = onlineUsers.getSelectionModel().getSelectedItem();
//            if (selectedUser != null) {
//                friendName = selectedUser;
//                nameLabel.setText(selectedUser);
//                onlineCircle.setOpacity(1);
//                chatArea.clear();
//                chatArea.appendText("You are now chatting with: " + selectedUser + "\n");
//            }
//        });
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
}