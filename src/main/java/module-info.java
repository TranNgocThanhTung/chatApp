module com.example.btck2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;
    requires javax.mail.api;
    requires jbcrypt;

    opens com.example.btck2 to javafx.fxml;
    opens com.example.btck2.Controller to javafx.fxml;

    exports com.example.btck2;
    exports com.example.btck2.Controller;
}