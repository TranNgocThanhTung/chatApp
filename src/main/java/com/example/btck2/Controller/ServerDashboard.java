
package com.example.btck2.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ServerDashboard implements Initializable {
    private List<ClientHandler> clients;
    @FXML
    private Button btnStartSV;
    @FXML
    private Button btnStop;
    @FXML
    private TextArea textareaSV;

    private ServerSocket serverSocket;
    private Thread serverThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textareaSV.setEditable(false);
        clients = new ArrayList<>();
    }

    public void startServer() {
        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(111);
                Platform.runLater(() -> {
                    textareaSV.appendText("Server started\n");
                    btnStartSV.setDisable(true);
                    btnStop.setDisable(false);
                });

                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                   new Thread(clientHandler).start();//Một luồng mới được khởi chạy để chạy đối tượng ClientHandler.
                    // Điều này cho phép server xử lý nhiều kết nối đồng thời mà không bị chặn bởi các thao tác I/O của từng client

                }
            } catch (IOException e) {
                Platform.runLater(() -> textareaSV.appendText("Error starting server: " + e.getMessage() + "\n"));
            }
        });
        serverThread.start();
    }

    @FXML
    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                Platform.runLater(() -> {
                    textareaSV.appendText("Server stopped.\n");
                    btnStartSV.setDisable(false);
                    btnStop.setDisable(true);
                });
            }
        } catch (IOException e) {
            Platform.runLater(() -> textareaSV.appendText("Error stopping server: " + e.getMessage() + "\n"));
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader is;
        private BufferedWriter os;
        private String username;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.os = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username= is.readLine();
            Platform.runLater(() -> textareaSV.appendText("Client connected: " + username + "\n"));
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = is.readLine()) != null) {

                    broadcastMessage(username+": "+message);
                }
            } catch (IOException e) {
                Platform.runLater(() -> textareaSV.appendText("Error: Unable to receive message from client\n"));
            }
        }

        public void sendMessage(String message) throws IOException {
            os.write(message);
            os.newLine();
            os.flush();
        }

        public void broadcastMessage(String message) {
            for (ClientHandler client : clients) {
                try {
                    if (client != this) { // Không gửi tin nhắn đến chính client gửi
                        client.sendMessage(message);
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> textareaSV.appendText("Error: Unable to send message to client\n"));
                }
            }
        }
    }
}
