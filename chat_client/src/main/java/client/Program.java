package client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name: ");
        String name = scanner.nextLine();
        try{
            InetAddress address = InetAddress.getLocalHost();
            System.out.println("Connet to: "+address+":"+1300);
            Socket socket = new Socket(address, 1300);
            Client client = new Client(socket, name);


            InetAddress inetAddress = socket.getInetAddress();
            System.out.println("Remote IP: " + inetAddress);
            String remoteIP = inetAddress.getHostAddress();
            System.out.println("Remote IP: " + remoteIP);
            System.out.println("LocalPort: " + socket.getLocalPort());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
