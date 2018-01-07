package sample.connectAPI;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.file.Files;

public class SenderFile extends Connector {

    private File file;

    SenderFile(File file, String ipAddress, int portNumber) {
        super(ipAddress, portNumber);
        this.file = file;
    }

    @Override
    public void run() {
//TODO size inner buffer protect
        try (Socket socket = new Socket(getIpAddress(), getPortNumber())) {

            if (socket.isConnected()) {
                System.out.println("Socket is connect");
            } else {
                return;
            }

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            byte[] bytesFile = prepareArrayBytesFromFile();

            if (bytesFile == null) {
                System.out.println("I cant get bytes from files");
                return;
            }

            byte[] header = prepareHeader(bytesFile.length);

            dataOutputStream.write(header);
            dataOutputStream.flush();
            dataOutputStream.write(bytesFile);
            dataOutputStream.flush();
            dataOutputStream.close();

        } catch (ConnectException e){
            System.out.println("Timeout SenderFile");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error in SenderFile");
            e.printStackTrace();
        }
    }

    private byte[] prepareArrayBytesFromFile() {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            System.out.println("Prepare data Error");
            e.printStackTrace();
        }
        return null;
    }

    private byte[] prepareHeader(int size) {
        byte[] header = new byte[100];
        String string = new String(header);
        System.out.println("header before " + string);

        String[] path = file.getAbsolutePath().split("/");
        String name = path[path.length - 1];
        byte[] nameBytes = name.getBytes();
        System.arraycopy(nameBytes, 0, header, 0, name.length());
        for (int i = 86; i < 100; i++) {
            int number = (int) (size / Math.pow(10, 100 - i - 1));
            if (number > 10) {
                number %= 10;
            }

            header[i] = (byte) (number + 48);
        }
        string = new String(header);
        System.out.println("header after " + string);
        return header;
    }
}
