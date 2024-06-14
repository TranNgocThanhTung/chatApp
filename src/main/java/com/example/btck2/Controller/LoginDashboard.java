
package com.example.btck2.Controller;

import com.example.btck2.Model.connectDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;

import com.example.btck2.Model.MailToken;
public class LoginDashboard implements Initializable {
    @FXML
    private static String Name;
    @FXML
    private TextField UNSignup;
    @FXML
    private PasswordField PWSignup;
    @FXML
    private Label labelSigup;
    @FXML
    private Button btnsignup;
    @FXML
    private TextField EMSignup;
    private Connection con;
    @FXML
    private TextField usernameTextfield;
    @FXML
    private PasswordField PasswordtextField;
    @FXML
    private Button btnsignin;
    @FXML
    private TextField yourname;
    @FXML
    private TextField ymail;

    private Alert alert;
    @FXML
    private Label forgotPW;
    @FXML
    private AnchorPane mainAC;
    @FXML
    private AnchorPane SCAC;

    public static String getname() {
        return Name;
    }

    @FXML
    void UserSignup(MouseEvent mouseEvent) {

        labelSigup.getScene().getWindow().hide();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/btck2/Signup.fxml"));
            Stage stage1 = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/btck2/Style.css").toExternalForm());
            stage1.initStyle(StageStyle.TRANSPARENT);

            stage1.setScene(scene);
            stage1.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void btnUserSignup(ActionEvent actionEvent) throws IOException, SQLException {

        con = connectDB.connect();
        String query = "INSERT INTO user (username, password, email,Name) VALUES (?, ?, ?,?)";
        if (UNSignup.getText().isEmpty() || PWSignup.getText().isEmpty() || EMSignup.getText().isEmpty() || yourname.getText().isEmpty()) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi!");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng điền tất cả thông tin");
            alert.show();
        } else {
            String hashedPassword = PasswordUtils.hashPassword(PWSignup.getText());
            PreparedStatement pr = con.prepareStatement(query);
            pr.setString(1, UNSignup.getText());
            pr.setString(2, hashedPassword);
            pr.setString(3, EMSignup.getText());
            pr.setString(4, yourname.getText());
            pr.executeUpdate();


            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo!");
            alert.setHeaderText(null);
            alert.setContentText("Đăng kí thành công!");
            alert.showAndWait();
            btnsignup.getScene().getWindow().hide();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/btck2/Login.fxml"));
            Stage stage1 = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/btck2/Style.css").toExternalForm());
            stage1.initStyle(StageStyle.TRANSPARENT);
            stage1.setScene(scene);
            stage1.show();

        }
        ;


    }

    @FXML
    public void Signin(ActionEvent actionEvent) throws SQLException, IOException {
        String query = "SELECT * FROM user WHERE username = ? ";
        PreparedStatement pr;
        if (usernameTextfield.getText().isEmpty() || PasswordtextField.getText().isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền tất cả thông tin");
        } else {
            con = connectDB.connect();
            pr = con.prepareStatement(query);
            pr.setString(1, usernameTextfield.getText());

            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (PasswordUtils.checkPassword(PasswordtextField.getText(), hashedPassword)) {
                    showAlert("Thông báo", "Đăng nhập thành công");
                    Name = rs.getString("Name");
                    btnsignin.getScene().getWindow().hide();
                    Parent root = FXMLLoader.load(getClass().getResource("/com/example/btck2/Client.fxml"));
                    Stage stage2 = new Stage();
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/com/example/btck2/Style.css").toExternalForm());
                    stage2.setScene(scene);
                    stage2.show();

                } else {
                    showAlert("Lỗi", "Tên đăng nhập hoặc mật khẩu không đúng");
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void forgotpw(MouseEvent mouseEvent) throws IOException {
        forgotPW.getScene().getWindow().hide();
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/btck2/ForgotPW.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/example/btck2/Style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();


    }

    public void TT(ActionEvent actionEvent) throws SQLException {
        String email = ymail.getText();
        if (email == null || email.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập email");
            return;
        }
        if (!MailToken.isEmailExist(email)) {
            showAlert("Error", "Email không tồn tại");
            return;
        }

        String token = MailToken.generateResetToken();
        MailToken.saveResetToken(email, token);
        MailToken.sendResetEmail(email, token);
        showAlert("Success", "Email đặt lại mật khẩu đã được gửi.");
//        mainAC.setVisible(false);
//        SCAC.setVisible(true);
    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}







