module org.example.chatapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires junit;


    opens org.example.chatapplication to javafx.fxml;
    exports org.example.chatapplication;
    exports test;
    opens test to javafx.fxml;
}