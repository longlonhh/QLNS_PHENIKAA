package vn.phenikaa.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import vn.phenikaa.database.NguoiDungDAO;
import vn.phenikaa.main.SessionManager;
import vn.phenikaa.person.NguoiDung;

public class LoginController {
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblMessage;

    private final NguoiDungDAO nguoiDungDAO = new NguoiDungDAO();

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        NguoiDung user = nguoiDungDAO.xacThuc(username, password);

        if (user != null) {
            SessionManager.login(user);
            navigateToMain();
        } else {
            lblMessage.setText("Sai tên đăng nhập hoặc mật khẩu!");
        }
    }

    private void navigateToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vn/phenikaa/ui/MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) txtUsername.getScene().getWindow();

            Scene scene = new Scene(root, 1000, 650);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            lblMessage.setText("Lỗi khi tải giao diện chính!");
        }
    }
}
