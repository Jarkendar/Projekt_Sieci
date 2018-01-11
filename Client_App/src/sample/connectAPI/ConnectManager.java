package sample.connectAPI;

import javax.sound.midi.Soundbank;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;

public class ConnectManager {
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT_NUMBER = 12345;

    private byte[] header = new byte[Connector.HEADER_SIZE];

    public void sendFileToCompress(File file) {
        if (file.isFile()) {
            //TODO change header to 110 size to for file
            
            new Thread(new SenderFile(prepareName(file), prepareArrayBytesFromFile(file), IP_ADDRESS, PORT_NUMBER)).start();
        } else {
            prepareArrayBytesFromDirectory(file);
            System.out.println("Header : = "+ new String(header));
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
        LinkedList<String> structure = new LinkedList<>();
        if (directory.isDirectory()) {
            structure.addLast("d_"+directory.getName());
            File[] filesInFolder = directory.listFiles();
            for (File file : filesInFolder) {
                pullFilesFromDirectory(filesToPrepare, file, structure, "\t");
            }
        } else {
            System.out.println("It is not directory");
        }

        System.out.println("File in folder");
        for (File file : filesToPrepare) {
            System.out.println(file);
        }

        String txt = String.join("\n",structure);
        System.out.println("My structure :\n" + txt+"\n size :"+txt.getBytes().length);

        LinkedList<byte[]> bytesToSend = new LinkedList<>();
        bytesToSend.addLast(txt.getBytes());

        long sizeFiles = 0L;
        for (File file : filesToPrepare){
            try {
                bytesToSend.addLast(Files.readAllBytes(file.toPath()));
                sizeFiles += bytesToSend.getLast().length;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Size all files : "+sizeFiles+"B");

        long allSize = txt.getBytes().length + sizeFiles;
        System.out.println("Size all to transfer :"+allSize);
        byte[] readyBytes = new byte[(int) allSize];
        int iter = 0;
        for (byte[] array : bytesToSend){
            System.out.println("Rozmiar :" + array.length);
            for (byte b : array){
                readyBytes[iter] = b;
                iter++;
            }
        }

        System.out.println("Ready size = "+readyBytes.length);

        prepareHeader(directory.getName(), (int)allSize+Connector.HEADER_SIZE, txt.getBytes().length);

        return readyBytes;
    }

    private void pullFilesFromDirectory(LinkedList<File> files, File file, LinkedList<String> structure, String prefix) {
        if (file.isFile()) {
            try {
                structure.addLast(prefix+file.getName()+" "+Files.readAllBytes(file.toPath()).length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            files.addLast(file);
        } else {
            structure.addLast(prefix+"d_"+file.getName());
            File[] filesInDirectory = file.listFiles();
            for (File childFile : filesInDirectory) {
                pullFilesFromDirectory(files, childFile,structure, prefix+"\t");
            }
        }
    }


    private void prepareHeader(String fileName, int size, int structureSize) {
        for (int i = 0 ; i<header.length ; i++){
            header[i] = (byte)'_';
        }
        String string = new String(header);
        System.out.println("header before " + string);

        byte[] nameBytes = fileName.replace("_","-").replace(" ","-").getBytes();
        for (int i = 0 ; i<nameBytes.length; i++){
            header[i] = nameBytes[i];
        }
        for (int i = 86; i < 100; i++) {
            int number = (int) (size / Math.pow(10, 100 - i - 1));
            if (number >= 10) {
                number %= 10;
            }
            header[i] = (byte) (number + 48);
        }

        for (int i = 101; i<110; i++){
            int number = (int) (structureSize / Math.pow(10, 110 - i - 1));
            if (number >= 10) {
                number %= 10;
            }
            header[i] = (byte) (number + 48);
        }

        string = new String(header);
        System.out.println("header after " + string);
    }


}
