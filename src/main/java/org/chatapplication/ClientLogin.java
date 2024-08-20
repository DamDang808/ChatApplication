package org.chatapplication;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.SQLException;

public class ClientLogin extends Application {

    @FXML
    private Text welcomeText;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button signInButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("client-login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 275);

        primaryStage.setOnHiding(event -> System.exit(0));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Login");
        primaryStage.show();
    }

    @FXML
    public void setSignInButtonOnClicked() throws IOException {
        // TODO:
        String username = usernameField.getText();
        String password = passwordField.getText();

        if(username.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error!", "Please enter your username", null);
            return;
        }
        if(password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error!", "Please enter a password", null);
            return;
        }

        Socket socket = new Socket();
        SocketAddress address = new InetSocketAddress(ConnectionUtil.host, ConnectionUtil.port);
        socket.connect(address, 30000);

        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.writeUTF(username + " " + password);

        DataInputStream input = new DataInputStream(socket.getInputStream());
        String response = input.readUTF();

        // If valid, open the chat application
        if (response.equals("valid")) {
            usernameField.setText("");
            passwordField.setText("");
            String token = input.readUTF();
            output.writeUTF(username);
            Platform.runLater(() -> {
                try {
                    new ClientApplication(username, token, socket, input, output).start(new Stage());
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            // If invalid, show an error message
            showAlert(Alert.AlertType.INFORMATION,"Failed", null, "Please enter correct username and Password");
        }
    }

    private static void showAlert (Alert.AlertType alertType, String title, String message, String headerText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.show();
    }
}
