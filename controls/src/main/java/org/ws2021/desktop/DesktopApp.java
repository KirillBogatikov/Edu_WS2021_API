package org.ws2021.desktop;

import java.io.IOException;

import org.ws2021.util.Extractor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DesktopApp extends Application {
    public static void main(String[] args) {
        Extractor.init(DesktopApp.class.getClassLoader());
        Application.launch(args);
    }
     
    @Override
    public void init() throws Exception {
        super.init();
    }
    
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Extractor.openURL("main.fxml"));
        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        System.out.println("shutdown");
        MainController.getInstance().stopAllServers();
        MainController.getInstance().exportData();
        System.exit(0);
    }
}
