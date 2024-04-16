package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable{
    private final Socket socket;
    private String userName;
    private BufferedWriter bw;
    private BufferedReader br;
    public static ArrayList<ClientManager> clients= new ArrayList<>();

    public ClientManager(Socket socket) throws IOException {
        this.socket = socket;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            userName = br.readLine();
            clients.add(this);
            System.out.println(userName + " connected to the chat.");
            broadcastMessage("Server: "+userName+" connected to the chat.");
        }catch (IOException e){
            closeEverything(socket,bw,br);
        }
    }
    private void removeClient(){
        clients.remove(this);
        System.out.println(userName+" leave this chat.");
    }

    private void closeEverything(Socket socket, BufferedWriter bw, BufferedReader br) {
        removeClient();
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

    private void broadcastMessage(String msg){
        for(ClientManager i: clients){
            try {
                if (!i.userName.equals(userName) && !msg.isEmpty()) {
                    i.bw.write(msg);
                    i.bw.newLine();
                    i.bw.flush();
                }
            }catch (IOException e){
                closeEverything(socket,bw,br);
            }
        }
    }

    @Override
    public void run() {
        String msgFromClient;
        while (socket.isConnected()){
            try {
                msgFromClient = br.readLine();
                broadcastMessage(msgFromClient);
            } catch (IOException e) {
                closeEverything(socket,bw,br);
                break;
            }
        }
    }
}
