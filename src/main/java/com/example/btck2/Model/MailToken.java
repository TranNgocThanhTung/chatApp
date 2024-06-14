package com.example.btck2.Model;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

public class MailToken {
    private static Connection con;
    public static void sendResetEmail(String email, String token) {
        // Thông tin SMTP server
        final String ym ="tungtnt.23ite@vku.udn.vn"; // email của bạn
        final String password = "sadaophai"; // mật khẩu email của bạn

        // Cấu hình các thuộc tính
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // host SMTP của bạn
        props.put("mail.smtp.port", "587"); // cổng SMTP của bạn

        // Tạo phiên làm việc với các thuộc tính và xác thực người dùng
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ym, password);
            }
        });

        try {
            // Tạo tin nhắn email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ym)); // địa chỉ email của bạn
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Đặt lại mật khẩu của bạn");
            message.setText("Để đặt lại mật khẩu của bạn, vui lòng sử dụng token sau: " + token);

            // Gửi tin nhắn
            Transport.send(message);

            System.out.println("Email đã được gửi đến " + email + " với token: " + token);

        } catch (MessagingException e) {

        }
    }


    public static boolean isEmailExist(String email) throws SQLException {
        con = connectDB.connect();
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        PreparedStatement pr = con.prepareStatement(query);
        pr.setString(1, email);
        ResultSet rs = pr.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }

        return false;
    }

    ;

    public static String generateResetToken() {
        // Tạo token ngẫu nhiên
        Random random = new Random();
        int token = 100000 + random.nextInt(9000);
        return String.valueOf(token);
    }

    ;

    public static void saveResetToken(String email, String token) throws SQLException {
        con = connectDB.connect();
        String qr = "UPDATE user SET reset_token = ? WHERE email = ?";
        PreparedStatement pr = con.prepareStatement(qr);
        pr.setString(1, token);
        pr.setString(2, email);
        pr.executeUpdate();

    }
}
