package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.InetAddress;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        String clientIPAddress = InetAddress.getLocalHost().getHostAddress().toString();//TODO change to my real IP address not address inner loop
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Client "+clientIPAddress);
        primaryStage.setScene(new Scene(root, 500, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
