package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;


public class Controller {
    public Button buttonSend;
    public Button buttonList;
    public Button buttonDownload;
    public Button buttonChooseFile;
    public ListView<String> listFiles;

    public void clickChooseFile(ActionEvent actionEvent) {
        actionEvent.getSource();
        System.out.println(actionEvent);
        //TODO get file path
    }

    public void clickSend(ActionEvent actionEvent) {
        actionEvent.getSource();
        System.out.println(actionEvent);
        //TODO send data to server
    }

    public void clickList(ActionEvent actionEvent) {
        actionEvent.getSource();
        System.out.println(actionEvent);
        //TODO send request for list
        //TODO receive list and decode his
        int size = 100;
        String array[] = new String[size];
        for (int i = 1; i<=size; i++){
            array[i-1] = "line "+i;
        }
        prepareDataToListView(array);
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
        //TODO send request with file name from list
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
