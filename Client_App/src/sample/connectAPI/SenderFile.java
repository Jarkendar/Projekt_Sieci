package sample.connectAPI;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.file.Files;

public class SenderFile extends Connector {

    private String fileName;
    private byte[] fileData;

    SenderFile(String fileName, byte[] fileData, String ipAddress, int portNumber) {
        super(ipAddress, portNumber);
        this.fileName = fileName;
        this.fileData = fileData;
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

            if (fileData == null) {
                System.out.println("I cant get bytes from files");
                return;
            }

            byte[] header = prepareHeader(fileData.length + HEADER_SIZE);

            //SEND sign, header and file to server
            dataOutputStream.write('c');
            dataOutputStream.flush();
            System.out.println("send c");
            dataOutputStream.write(header);
            dataOutputStream.flush();
            System.out.println("send header");
            dataOutputStream.write(fileData);
            dataOutputStream.flush();
            System.out.println("send file");
            dataOutputStream.close();

        } catch (ConnectException e) {
            System.out.println("Timeout SenderFile");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error in SenderFile");
            e.printStackTrace();
        }
    }

    private byte[] prepareHeader(int size) {
        byte[] header = new byte[100];
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
        string = new String(header);
        System.out.println("header after " + string);
        return header;
    }
}
