package sample.connectAPI;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Stack;

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

            byte[] headerDownload = new byte[HEADER_SIZE];
            inputStream.read(headerDownload, 0, HEADER_SIZE);

            int size = pullDownloadSize(new String(headerDownload));

            while (true) {
                int readBytes = inputStream.read(buffer);
                if (readBytes < 0) {
                    break;
                }
                byteArrayOutputStream.write(buffer);
            }
            byte[] downloadData = byteArrayOutputStream.toByteArray();
            byte[] dataArray = new byte[size];

            for (int i = 0; i < size; i++) {
                dataArray[i] = downloadData[i];
            }

            System.out.println(new String(headerDownload));

            System.out.println("Download bytes : " + dataArray.length);

            int structureSize = pullStructureSize(new String(headerDownload));
            if (structureSize == 0) {
                makeFile(pullNameFromHeader(new String(headerDownload)), dataArray);
            } else {
                System.out.println("Structure size : " + structureSize);
                makeStructureFiles(structureSize, dataArray);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String pullNameFromHeader(String header) {
        String[] word = header.split("_");
        return word[0];
    }

    private int pullStructureSize(String header) {
        String[] word = header.split("_");
        return Integer.parseInt(word[word.length - 1]);
    }

    private int pullDownloadSize(String header) {
        String[] word = header.split("_");
        return Integer.parseInt(word[word.length - 2]) - HEADER_SIZE;
    }

    private void makeFile(String fileName, byte[] fileData) {
        try {
            File file = new File("download" + fileName);
            Path path = Paths.get(file.getAbsolutePath());
            System.out.println(path);
            Files.write(path, fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeStructureFiles(int structureSize, byte[] data) {
        System.out.println("Make structure");
        System.out.println(structureSize);
        System.out.println(data.length);
        byte[] structureData = new byte[structureSize];
        System.arraycopy(data, 0, structureData, 0, structureSize);
        System.out.println(new String(structureData));
        byte[] filesData = new byte[data.length - structureSize];
        System.arraycopy(data, 0 + structureSize, filesData, 0, filesData.length);

        String[] structure = new String(structureData).split("\n");

        int actualNesting = 0;
        int usedBytes = 0;

        LinkedList<String> folderList = new LinkedList<>();
        for (String row : structure) {
            int nesting = row.split("\t").length-1;
            System.out.println("nesting ="+nesting);
            if (nesting < actualNesting) {
                folderList.removeLast();
                actualNesting--;
            }
            String pathPrefix = makePrefixPath(folderList);

            if ("d_".equals(row.replace("\t","").substring(0, 2))) {//makeFolder
                String folderName = row.replace("\t","").substring(2) + "test";
                System.out.println(folderName);
                System.out.println(pathPrefix+folderName);
                new File(pathPrefix+folderName).mkdir();
                folderList.addLast(folderName);
                actualNesting++;
            } else {
                System.out.println("It is file");
            }
        }
    }

    private String makePrefixPath(LinkedList<String> folderList) {
        String path = "";
        for (String folder : folderList) {
            path = path + folder + "/";
        }
        return path;
    }
}
