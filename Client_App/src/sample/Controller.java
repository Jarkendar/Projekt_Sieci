package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import sample.connectAPI.ConnectManager;

import java.io.File;


public class Controller {
    public Button buttonSend;
    public Button buttonList;
    public Button buttonDownload;
    public Button buttonChooseFile;
    public ListView<String> listFiles;

    private File file;
    private ConnectManager connectManager = new ConnectManager();
    private String[] listForListView;

    public void clickChooseFile(ActionEvent actionEvent) {
        actionEvent.getSource();
        System.out.println(actionEvent);
        file = Main.openFileChooser();
        if (file != null) {
            buttonChooseFile.setText(file.getAbsolutePath());
        }
    }

    public void clickSend(ActionEvent actionEvent) {
        actionEvent.getSource();
        System.out.println(actionEvent);
        if (file != null) {
            connectManager.sendFileToCompress(file);
            System.out.println("Zleciłem");
        }
        file = null;
        buttonChooseFile.setText("Choose File");
    }

    public void clickList(ActionEvent actionEvent) {
        actionEvent.getSource();
        System.out.println(actionEvent);

        listForListView = connectManager.getListFileToDownload();

        int size = 100;
        listForListView = new String[size];
        for (int i = 1; i<=size; i++){
            listForListView[i-1] = "line "+i;
        }
        prepareDataToListView(listForListView);
    }

    public void clickDownload(ActionEvent actionEvent) {
        actionEvent.getSource();
        System.out.println(actionEvent);
        if (listFiles.getSelectionModel().getSelectedItem() != null){
            String lines = listFiles.getSelectionModel().getSelectedItem();
            System.out.println(lines);
        }else {
            System.out.println("Not choose file from list.");
        }
        //TODO sendFileToCompress request with file name from list
        //TODO download stream
        //TODO decode stream and save file
    }

    public void prepareDataToListView(String lines[]){
        ObservableList<String> listTest = FXCollections.observableArrayList(lines);
        setListText(listTest);
    }

    private void setListText(ObservableList<String> list){
        listFiles.setItems(list);
    }

}
