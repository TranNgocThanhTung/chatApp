
package com.example.btck2.Controller;

import com.example.btck2.Model.connectDB;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.*;
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
    private Connection conn;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textareaSV.setEditable(false);
        clients = new ArrayList<>();

    }
//chạy một luồng mới (serverThread) để xử lý các hoạt động của máy chủ, bao gồm chấp nhận kết nối từ client và khởi chạy một luồng mới cho mỗi client bằng cách tạo đối tượng ClientHandler và khởi chạy nó trong một luồng mới.
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
            sendChatHistory();
        }

        private void sendChatHistory() {
            List<String> chatHistory = loadChatHistoryFromDatabase();
            try {
                for (String message : chatHistory) {
                    os.write(message);
                    os.newLine();
                }
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = is.readLine()) != null) {
                     saveMessageToDatabase(username,message);
                    broadcastMessage(username+": "+message);
                }
            } catch (IOException e) {
                Platform.runLater(() -> textareaSV.appendText(username+" "+"đã ngắt kết nối"+ "\n"));
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
                  e.printStackTrace();
                }
            }
        }
        private void saveMessageToDatabase(String username, String message) {
            conn= connectDB.connect();
            String query = "INSERT INTO chat_history (username, message) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, message);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private List<String> loadChatHistoryFromDatabase() {
        conn= connectDB.connect();
        List<String> chatHistory = new ArrayList<>();
        String query = "SELECT username, message, timestamp FROM chat_history ORDER BY timestamp ASC";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String message = resultSet.getString("message");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                chatHistory.add("[" + timestamp + "] " + username + ": " + message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chatHistory;
    }
}
