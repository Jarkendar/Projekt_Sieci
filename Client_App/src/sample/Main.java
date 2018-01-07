package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.InetAddress;

public class Main extends Application {

    private static final FileChooser fileChooser = new FileChooser();
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        String clientIPAddress = InetAddress.getLocalHost().getHostAddress().toString();//TODO change to my real IP address not address inner loop
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Client "+clientIPAddress);
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }

    public static File openFileChooser(){
        File file = fileChooser.showOpenDialog(stage);
        System.out.println(file);
        return file;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
