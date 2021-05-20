import socket.SocketClient;

import java.io.IOException;

public class Main {

    public static final String ip = "localhost";

    public static final int port = 1337;

    public static void main(String args[]) {
        try {
            SocketClient socketClient = new SocketClient(ip, port);
            socketClient.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
