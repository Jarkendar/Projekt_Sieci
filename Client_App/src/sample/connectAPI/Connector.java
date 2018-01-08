package sample.connectAPI;

public abstract class Connector implements Runnable {
    private String ipAddress;
    private int portNumber;

    public static final int HEADER_SIZE = 100;

    Connector(String ipAddress, int portNumber) {
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }
}
