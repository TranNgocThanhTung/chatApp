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
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


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

    private  Alert alert;

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

    con=connectDB.connect();
    String query= "INSERT INTO user (username, password, email,Name) VALUES (?, ?, ?,?)";
    if(UNSignup.getText().isEmpty()|| PWSignup.getText().isEmpty()){
        alert=new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi!");
        alert.setHeaderText(null);
        alert.setContentText("Vui lòng điền tất cả thông tin");
        alert.show();
    }
    else{
         PreparedStatement pr= con.prepareStatement(query);
             pr.setString(1,UNSignup.getText());
             pr.setString(2,PWSignup.getText());
             pr.setString(3,EMSignup.getText());
             pr.setString(4,yourname.getText());
             pr.executeUpdate();


        alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo!");
        alert.setHeaderText(null);
        alert.setContentText("Đăng kí thành công!");
        alert.showAndWait();
        btnsignup.getScene().getWindow().hide();
        Parent root =FXMLLoader.load(getClass().getResource("/com/example/btck2/Login.fxml"));
        Stage stage1 = new Stage();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/example/btck2/Style.css").toExternalForm());
        stage1.initStyle(StageStyle.TRANSPARENT);
        stage1.setScene(scene);
        stage1.show();

        };


    }
    @FXML
    public void Signin(ActionEvent actionEvent) throws SQLException, IOException {
      String query= "SELECT * FROM user WHERE username = ? AND password = ?";
      PreparedStatement pr;
      if(usernameTextfield.getText().isEmpty()||PasswordtextField.getText().isEmpty()){
          alert=new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Lỗi!");
          alert.setHeaderText(null);
          alert.setContentText("Vui lòng điền tất cả thông tin");
          alert.show();

      }
      else{
        con=connectDB.connect();
        pr=con.prepareStatement(query);
        pr.setString(1,usernameTextfield.getText());
        pr.setString(2,PasswordtextField.getText());
        ResultSet rs=pr.executeQuery();
        if(rs.next()){
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo!");
            alert.setHeaderText(null);
            alert.setContentText("Đăng nhập thành công!");
            alert.showAndWait();
            Name = rs.getString("Name");
            btnsignin.getScene().getWindow().hide();

     Parent root=FXMLLoader.load(getClass().getResource("/com/example/btck2/Client.fxml"));
     Stage stage2=new Stage();
     Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/example/btck2/Style.css").toExternalForm());
     stage2.setScene(scene);
     stage2.show();

        }else{ alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi!");
            alert.setHeaderText(null);
            alert.setContentText("Tên đăng nhập hoặc mật khẩu không đúng!");
            alert.showAndWait();

        }

      }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    public void forgotpw(MouseEvent mouseEvent) {
    }
}




