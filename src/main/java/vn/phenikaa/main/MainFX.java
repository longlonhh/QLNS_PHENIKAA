package vn.phenikaa.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/vn/phenikaa/ui/MainView.fxml")
        );

        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 650);

        // 🔥 GẮN CSS (QUAN TRỌNG)
        scene.getStylesheets().add(
            getClass().getResource("/css/style.css").toExternalForm()
        );

        stage.setTitle("QUẢN LÝ NHÂN SỰ - PHENIKAA");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
