package sample.connectAPI;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class FileServerLister extends Connector{

    private String[] list;

    FileServerLister(String ipAddress, int portNumber) {
        super(ipAddress, portNumber);
    }

    @Override
    public void run() {
        //TODO size inner buffer protect
        try(Socket socket = new Socket(getIpAddress(),getPortNumber())) {
            if (socket.isConnected()){
                System.out.println("Socket is connect");
            }else {
                return;
            }

            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            byte[] bytes = {(byte)'w'};
            dataOutputStream.write(bytes);
            dataOutputStream.flush();
            System.out.println("Send w request");

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[8192];//TODO to change
            InputStream inputStream = socket.getInputStream();

            while (true){
                int readBytes = inputStream.read(buffer);
                if (readBytes < 0){
                    break;
                }
                byteArrayOutputStream.write(buffer,0,readBytes);
            }

            byte[] dataArray = byteArrayOutputStream.toByteArray();

            list =  decodeStream(dataArray);

            notifyAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] decodeStream(byte dataArray[]){
        String downloadData = new String(dataArray);
        return downloadData.split("\n");
    }

    public String[] getList() {
        return list;
    }
}
