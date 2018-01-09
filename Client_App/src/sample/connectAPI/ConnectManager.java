package sample.connectAPI;

import java.io.File;

public class ConnectManager {
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT_NUMBER = 12345;

    public void sendFileToCompress(File file) {
        new Thread(new SenderFile(file, IP_ADDRESS, PORT_NUMBER)).start();
    }

    public String[] getListFileToDownload(){
        FileServerLister fileServerLister = new FileServerLister(IP_ADDRESS,PORT_NUMBER);
        Thread thread = new Thread(fileServerLister);
        thread.start();
        synchronized (thread) {
            try {
                System.out.println("Wait for list.");
                thread.wait();
            }catch (InterruptedException e){
                System.out.println("Error in wait thread.");
                e.printStackTrace();
            }
        }
        return fileServerLister.getList();
    }

    public void requestFileToDecompress(String name){
        new Thread(new ReceiverFile(name, IP_ADDRESS, PORT_NUMBER)).start();
    }

}
