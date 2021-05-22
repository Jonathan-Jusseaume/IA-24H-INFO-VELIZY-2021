import socket.SocketClient;
import utils.ParcoursLargeur;

import java.io.IOException;

public class Main {

    public static final String ip = "localhost";

    public static final int port = 2121;

    public static void main(String args[]) throws Exception {
        try {
            SocketClient socketClient = new SocketClient(ip, port);
            socketClient.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
