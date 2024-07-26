module org.chatapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.zaxxer.hikari;
    requires org.slf4j;
    requires mysql.connector.j;
    requires json.simple;

    opens org.chatapplication to javafx.fxml;
    exports org.chatapplication;
}