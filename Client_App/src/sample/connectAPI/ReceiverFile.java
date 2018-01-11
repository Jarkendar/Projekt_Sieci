package sample.connectAPI;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReceiverFile extends Connector {

    private String wantFile;

    public ReceiverFile(String wantFile, String ipAddress, int portNumber) {
        super(ipAddress, portNumber);
        this.wantFile = wantFile;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(getIpAddress(), getPortNumber())) {
            byte[] header = wantFile.getBytes();
            System.out.println("Header : " + new String(header));
            System.out.println("Header size : " + header.length);

            //SEND name to server
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.write('d');
            dataOutputStream.flush();
            dataOutputStream.write(header);
            //RECEIVE data from server
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[8192];
            InputStream inputStream = socket.getInputStream();

            byte[] headerDownload = new byte[100];
            inputStream.read(headerDownload, 0, HEADER_SIZE);
            String[] head = new String(headerDownload).split("_");
            int size = Integer.parseInt(head[head.length-1]) -100;

            while (true) {
                int readBytes = inputStream.read(buffer);
                if (readBytes < 0) {
                    break;
                }
                byteArrayOutputStream.write(buffer);
            }
            byte[] downloadData = byteArrayOutputStream.toByteArray();
            byte[] dataArray = new byte[size];

            for (int i = 0; i<size; i++){
                dataArray[i] = downloadData[i];
            }

            System.out.println(new String(headerDownload));

            System.out.println("Download bytes : " + dataArray.length);
            String fileName = pullNameFromHeader(new String(headerDownload));
            File file = new File("download" + fileName);

            Path path = Paths.get(file.getAbsolutePath());
            System.out.println(path);
            Files.write(path, dataArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String pullNameFromHeader(String header) {
        String[] word = header.split("_");
        return word[0];
    }
}
