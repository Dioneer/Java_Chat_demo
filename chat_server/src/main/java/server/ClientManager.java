package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

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
        String[] arr = msg.split("@");
        System.out.println("Split" + Arrays.toString(arr));
        if(arr.length>2){
            boolean flag = false;
            String from = arr[1].trim();
            String to= arr[2].trim();
            String personalMsg = arr[3].trim();
            System.out.println(from+"from "+to+" to "+personalMsg+" personalMs ");
            for(ClientManager i: clients){
                try {
                    if (i.userName.equals(to) && !msg.isEmpty()) {
                        i.bw.write(from+" "+personalMsg);
                        i.bw.newLine();
                        i.bw.flush();
                        flag = true;
                    }
                }catch (IOException e){
                    closeEverything(socket,bw,br);
                }
            }
            if(!flag) {
                try {
                    this.bw.write("Uncorrected receiver");
                    this.bw.newLine();
                    this.bw.flush();
                } catch (IOException e) {
                    closeEverything(socket, bw, br);
                }
            }
        }else {
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
