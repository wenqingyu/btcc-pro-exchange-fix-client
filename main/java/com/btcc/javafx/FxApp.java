package com.btcc.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.net.URL;

/**
 * Created by zhenning on 15/8/29.
 */
public class FxApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("FIX Client for BTCC forwards");

        Parent root = FXMLLoader.load(this.getClass().getResource("forwardclient.fxml"));

        Scene scene = new Scene(root, 1050, 760);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
