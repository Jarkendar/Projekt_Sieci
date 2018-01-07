package sample.connector;

import java.io.File;

public class ConnectManager {
    private static final String IP_ADDRESS = "192.0.0.1";
    private static final int PORT_NUMBER = 12345;

    public void send(File file) {
        new Thread(new Sender(file, IP_ADDRESS, PORT_NUMBER)).start();
    }

}
