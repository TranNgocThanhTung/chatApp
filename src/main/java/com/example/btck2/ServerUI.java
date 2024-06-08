package com.example.btck2;

import com.example.btck2.Model.connectDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ServerUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root= FXMLLoader.load(getClass().getResource("/com/example/btck2/Server.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setTitle("Server");
        stage.show();
    }
}
