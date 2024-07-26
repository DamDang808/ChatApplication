package org.chatapplication;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public void setSignInButtonOnClicked() throws SQLException {
        // TODO:
        String username = usernameField.getText();
        String password = passwordField.getText();

        if(username.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error!", "Please enter your email id", null);
            return;
        }
        if(password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error!", "Please enter a password", null);
            return;
        }

        // Connect to database
        Connection connection = DataSource.getConnection();
        // Check username and password
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE Username = ? AND Password = ?");
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);

        ResultSet resultSet = preparedStatement.executeQuery();
        // If valid, open the chat application
        if (resultSet.next()) {
            usernameField.setText("");
            passwordField.setText("");
            Platform.runLater(() -> {
                try {
                    new ClientApplication(resultSet.getInt(1), username).start(new Stage());
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            // If invalid, show an error message
            showAlert(Alert.AlertType.INFORMATION,"Failed", null, "Please enter correct Email and Password");
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
