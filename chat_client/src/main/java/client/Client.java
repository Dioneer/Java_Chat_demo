package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final Socket socket;
    private final String userName;
    private BufferedWriter bw;
    private BufferedReader br;

    public Client(Socket socket, String userName) {
        this.socket = socket;
        this.userName = userName;
        try{
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        }catch (IOException ex){
            closeEverything(socket,bw,br);
        }
    }

    public void sendMessage() {
        try {
            bw.write(userName);
            bw.newLine();
            bw.flush();
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                String msg = scanner.nextLine();
                bw.write(userName+": "+msg);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            closeEverything(socket,bw,br);
        }
    }

    public void listenForMessage(){
        new  Thread(()->{
            String message;
            while (socket.isConnected()){
                try {
                    message = br.readLine();
                    System.out.println(message);
                } catch (IOException e) {
                    closeEverything(socket,bw,br);
                }
            }
        }).start();

    }
    private void closeEverything(Socket socket, BufferedWriter bw, BufferedReader br) {
        try {
        if(br != null){
            br.close();
        }
        if(bw != null){
            bw.close();
        }
        if(socket != null)
            bw.close();
        } catch (IOException e) {
                throw new RuntimeException(e);
        }
    }
}
