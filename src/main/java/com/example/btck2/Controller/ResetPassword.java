package com.example.btck2.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import com.example.btck2.Model.connectDB;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ResetPassword implements Initializable {
    @FXML
    private PasswordField newpw;
    @FXML
    private PasswordField xacnhanpw;
    String password;
    String xnpw;
    private Connection con;
    @FXML
    private Button xacnhan;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
 password=newpw.getText();
 xnpw=xacnhanpw.getText();
    }

    public void XN(ActionEvent actionEvent) throws SQLException, IOException {
        password=newpw.getText();
        xnpw=xacnhanpw.getText();
        if (newpw.getText().isEmpty() || xacnhanpw.getText().isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền tất cả thông tin");
        } else if(!password.equals(xnpw)){
            showAlert("Lỗi","Mật khẩu xác nhận không khớp");
        }
        else {
            con = connectDB.connect();
            String hashedPassword = PasswordUtils.hashPassword(xnpw);
            String qr = "UPDATE user SET password=? WHERE email=?";
            PreparedStatement pr = con.prepareStatement(qr);
            pr.setString(1, hashedPassword);
            pr.setString(2,LoginDashboard.getemail());
            pr.executeUpdate();
            showAlert("Thông báo", "Đổi mật khẩu thành công");
            xacnhan.getScene().getWindow().hide();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/btck2/Login.fxml"));
            Stage stage1 = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/btck2/Style.css").toExternalForm());
            stage1.initStyle(StageStyle.TRANSPARENT);
            stage1.setScene(scene);
            stage1.show();
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
