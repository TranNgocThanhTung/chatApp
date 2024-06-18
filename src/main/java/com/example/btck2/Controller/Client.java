package com.example.btck2.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;


import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;


public class Client implements Initializable {
    private boolean connected = false;
    @FXML
    private TextField msf;
    private Connection con;
    private DataInputStream dis;
    @FXML
    private TextArea TA;
    @FXML
    private TextArea TA2;
    private LoginDashboard lg;
    private List<File> selectedFiles;
    private Socket socket;
    private ServerDashboard serverDashboard;
    private BufferedWriter os;
    private BufferedReader is;
    @FXML
    private Label nameuser;
    private Alert alert;

    public void setServerDashboard(ServerDashboard serverDashboard) {
        this.serverDashboard = serverDashboard;
    }

    public Client() {
        connect(); // Gọi phương thức connect khi khởi tạo đối tượng Client

    }

    @FXML
    public void connect() {
// khởi chạy một luồng mới để kết nối client tới máy chủ. Sau khi kết nối, một luồng khác được khởi chạy để nhận tin nhắn từ máy chủ
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 111);
                os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                connected = true;

                sendUsernameToServer(LoginDashboard.getname());
                new Thread(this::receiveMessages).start();// khởi chạy luồng để nhận tin nhắn
            } catch (IOException e) {
                Platform.runLater(() -> showAlert("Connection Failed", "Could not connect to server "));
            }

        }).start();
    }


    @FXML

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (nameuser != null) {
            nameuser.setText(LoginDashboard.getname());
        } else {
            System.err.println("nameuser is null");
        }
        TA.setEditable(false);
        TA2.setEditable(false);
    }

    public void sendMessgae(ActionEvent actionEvent) {
        String message = msf.getText();
        if (message != null && !message.isEmpty()) {
            new Thread(() -> {
                try {
                    os.write(message);
                    os.newLine();
                    os.flush();
                    Platform.runLater(() -> TA.appendText("Bạn: " + message + "\n"));
                    msf.clear();
                } catch (IOException e) {
                    Platform.runLater(() -> showAlert("Lỗi", "Không thể gửi tin nhắn"));
                }
            }).start();
        }
    }

    private void sendUsernameToServer(String username) throws IOException {
        os.write(username);
        os.newLine();
        os.flush();
    }

    private void receiveMessages() {
        String message;
        try {
            while ((message = is.readLine()) != null) {

                String finalMessage = message;
                Platform.runLater(() -> TA.appendText(finalMessage + "\n"));
            }
        } catch (IOException e) {
            Platform.runLater(() -> showAlert("Lỗi", "Không thể nhận tin nhắn từ server"));
        }
    }

    private void showAlert(String title, String message) {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


