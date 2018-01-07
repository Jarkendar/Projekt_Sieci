package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;


public class Controller {
    public Button buttonSend;
    public Button buttonList;
    public Button buttonDownload;
    public Button buttonChooseFile;
    public ListView listFiles;

    public void clickChooseFile(ActionEvent actionEvent) {
        actionEvent.getSource();
        System.out.println(actionEvent);
    }

    public void clickSend(ActionEvent actionEvent) {
        actionEvent.getSource();
        System.out.println(actionEvent);
    }

    public void clickList(ActionEvent actionEvent) {
        actionEvent.getSource();
        System.out.println(actionEvent);
    }

    public void clickDownload(ActionEvent actionEvent) {
        actionEvent.getSource();
        System.out.println(actionEvent);
    }
}
