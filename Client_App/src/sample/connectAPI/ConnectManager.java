package sample.connectAPI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;

public class ConnectManager {
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT_NUMBER = 12345;

    public void sendFileToCompress(File file) {
        if (file.isFile()) {
            new Thread(new SenderFile(prepareName(file), prepareArrayBytesFromFile(file), IP_ADDRESS, PORT_NUMBER)).start();
        } else {
            prepareArrayBytesFromDirectory(file);
        }

    }

    public String[] getListFileToDownload() {
        FileServerLister fileServerLister = new FileServerLister(IP_ADDRESS, PORT_NUMBER);
        Thread thread = new Thread(fileServerLister);
        thread.start();
        synchronized (thread) {
            try {
                System.out.println("Wait for list.");
                thread.wait();
            } catch (InterruptedException e) {
                System.out.println("Error in wait thread.");
                e.printStackTrace();
            }
        }
        return fileServerLister.getList();
    }

    public void requestFileToDecompress(String name) {
        new Thread(new ReceiverFile(name, IP_ADDRESS, PORT_NUMBER)).start();
    }

    public void sendDirecotryForCompress(File directory) {
        System.out.println("send directory");
    }

    private byte[] prepareArrayBytesFromFile(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            System.out.println("Prepare data Error");
            e.printStackTrace();
        }
        return null;
    }

    private String prepareName(File file) {
        String[] path = file.getAbsolutePath().split("/");
        return path[path.length - 1];
    }

    private byte[] prepareArrayBytesFromDirectory(File directory) {
        LinkedList<File> filesToPrepare = new LinkedList<>();
        //TODO build structure file and save to files LinkedList<String> structure =
        if (directory.isDirectory()) {
            File[] filesInFolder = directory.listFiles();
            for (File file : filesInFolder) {
                pullFilesFromDirectory(filesToPrepare, file);
            }
        } else {
            System.out.println("It is not directory");
        }

        System.out.println("File in folder");
        for (File file : filesToPrepare) {
            System.out.println(file);
        }

        return "".getBytes();
    }

    private void pullFilesFromDirectory(LinkedList<File> files, File file) {
        if (file.isFile()) {
            files.addLast(file);
        } else {
            File[] filesInDirectory = file.listFiles();
            for (File childFile : filesInDirectory) {
                pullFilesFromDirectory(files, childFile);
            }
        }
    }


}
