module org.chatapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.zaxxer.hikari;
    requires mysql.connector.j;
    requires com.google.common;

    opens org.chatapplication to javafx.fxml;
    exports org.chatapplication;
}