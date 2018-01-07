package sample.connectAPI;

public abstract class Connector implements Runnable {
    private String ipAddress;
    private int portNumber;

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
