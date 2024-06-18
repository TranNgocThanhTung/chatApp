
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
    private static String email;
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
    @FXML
    private TextField token;
    private Alert alert;
    @FXML
    private Label forgotPW;
    @FXML
    private AnchorPane mainAC;
    @FXML
    private AnchorPane SCAC;
@FXML
private Button ok;
public static String getemail(){
    return email;
}
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
           showAlert("Lỗi","Vui lòng điền tất cả thông tin");
        } else {
            String hashedPassword = PasswordUtils.hashPassword(PWSignup.getText());
            PreparedStatement pr = con.prepareStatement(query);
            pr.setString(1, UNSignup.getText());
            pr.setString(2, hashedPassword);
            pr.setString(3, EMSignup.getText());
            pr.setString(4, yourname.getText());
            pr.executeUpdate();
            showAlert("Thông báo","Đăng kí thành công");
            btnsignup.getScene().getWindow().hide();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/btck2/Login.fxml"));
            Stage stage1 = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/btck2/Style.css").toExternalForm());
            stage1.initStyle(StageStyle.TRANSPARENT);
            stage1.setScene(scene);
            stage1.show();

        }

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
@FXML
    public void TT(ActionEvent actionEvent) throws SQLException {
         email = ymail.getText();
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
        mainAC.setVisible(false);
        SCAC.setVisible(true);
    }


    public void OK(ActionEvent actionEvent) throws SQLException, IOException {
    String tk=token.getText();
        if (tk == null || tk.isEmpty()) {
            showAlert("Error", "Vui lòng nhập token");
            return;
        }

        if (isTokenValid(tk)) {
            Parent root=FXMLLoader.load(getClass().getResource("/com/example/btck2/ResetPassword.fxml"));
            Scene scene=new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/btck2/Style.css").toExternalForm());
            Stage st=new Stage();
            st.setScene(scene);
            showAlert("Success", "Mã token hợp lệ");
            ok.getScene().getWindow().hide();
            st.show();

        } else {
            showAlert("Error", "Token không hợp lệ");
        }

    }
    private boolean isTokenValid(String token) throws SQLException {
        // Kiểm tra token trong cơ sở dữ liệu
        String qr="SELECT COUNT(*) FROM user WHERE reset_token = ?";
             con=connectDB.connect();
             PreparedStatement stmt = con.prepareStatement(qr) ;
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        return false;
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}







