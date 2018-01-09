package sample.connectAPI;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ReceiverFile extends Connector{

    private String wantFile;

    public ReceiverFile(String wantFile, String ipAddress, int portNumber) {
        super(ipAddress, portNumber);
        this.wantFile = wantFile;
    }

    @Override
    public void run() {
     try (Socket socket = new Socket(getIpAddress(),getPortNumber())){
         byte[] header = wantFile.getBytes();
         System.out.println("Header : "+new String(header));
         System.out.println("Header size : "+header.length);

         //SEND name to server
         DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
         dataOutputStream.write('d');
         dataOutputStream.flush();
         dataOutputStream.write(header);
        //RECEIVE data from server
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

         byte[] buffer = new byte[8192];
         InputStream inputStream = socket.getInputStream();

         while (true){
             int readBytes = inputStream.read(buffer);
             if (readBytes < 0){
                 break;
             }
             byteArrayOutputStream.write(buffer,0,readBytes);
         }
         byte[] dataArray = byteArrayOutputStream.toByteArray();

         System.out.println(new String(dataArray, 0, 100));

         System.out.println(dataArray.length);
         //TODO save stream to file
     }catch (IOException e){
         e.printStackTrace();
     }
    }
}
