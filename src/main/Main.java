package main;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
    	URL url = new File("src/gui/pages/main_view.fxml").toURI().toURL();
    	Parent root = FXMLLoader.load(url);

        // Set up the primary stage
        primaryStage.setTitle("Main Stage");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
