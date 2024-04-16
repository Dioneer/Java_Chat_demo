package client;

import java.net.Socket;

public class Client {
    private final Socket socket;
    private final String userName;

    public Client(Socket socket, String userName) {
        this.socket = socket;
        this.userName = userName;
    }

    public void sendMessage(){

    }
    public void listenForMessage(){

    }
}
