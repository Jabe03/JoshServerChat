

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

import javax.swing.text.html.HTMLDocument.HTMLReader.HiddenAction;

public class Client implements Host {
    static int numberOfAttempts;

    public static void main(String[] args) throws Exception {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("What is the IP?");
//        String addr = sc.nextLine();
//        System.out.println("Whats is the port?");
//        int port = sc.nextInt();
//        Client c = new Client(addr, port);
        Client c = new Client();

    }

    private Socket sr;
    private InputStream is;
    private OutputStream os;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    String clientName;
    UUID clientId;
    ChatWindow cw;

    public Client() throws IOException {
        cw = new ChatWindow(this);
        //startRuntimeChat();
    }
    public void startRuntimeChat()throws IOException{
        System.out.println("Starting chat....");
        sendMessage(new Message(clientName, null));
        clientId  = (UUID)readObject();
        cw.userId = clientId;
        Thread t = new Thread(() -> {

            sendMessage(new Message("", clientId, "userJoining"));

            while(true){
                try {
                    //System.out.println("Listening for data...");
                    Object o = ois.readObject();
                    process(o);
                } catch(SocketException e){
                    cw.addMessage(new Message("Lost connection to host...", null, "chatWindowMessage"));
                    return;
                } catch (IOException | ClassNotFoundException e){
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
        t.setName("Chat listener");
        t.start();
    }
    public void process(Object message){
        if(message instanceof Message m){

            if(m.isTextMessage()){
                System.out.println("wtf to do with this " + m);
            } else {
                if(m.getText().equals("updatedMessages")){
                    //System.out.println("Updated messages: " + m.getObjectMessage());
                    cw.setMessages((ArrayList<Message>)m.getObjectMessage());
                } else if (m.getText().equals("updatedClients")){
                    cw.setParticipants((HashMap<UUID, String>)m.getObjectMessage());
                }
            }
        } else if(message instanceof UUID id ){

            this.clientId = id;
            System.out.println(id);
        }
    }
    public boolean establishConnection(String address, int port) throws IOException {
        try {
            sr = new Socket(address, port);
        } catch (ConnectException e){
            cw.tryConnectAgain();
            return false;
        } catch (UnknownHostException e){
            cw.tryServerInputAgain();
            return false;
        }
        is = sr.getInputStream();
        os = sr.getOutputStream();
        oos = new ObjectOutputStream(os);
        ois = new ObjectInputStream(is);
        return true;
    }

//    public void sendMessage(String msg) throws IOException {
//
//        byte[] b = msg.getBytes();
//        os.write(b);
//        System.out.println("Message sent!");
//        System.out.println("MSg sent!");
//
//    }

    public Object readObject(){
        try{
            return ois.readObject();
        } catch (ClassNotFoundException | IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public void sendObject(Object o)  {
        try {
            oos.writeObject(o);
        } catch(SocketException e){
            return;//connection reset, cant do anything
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    public void stop(){
        System.exit(1);
    }
    @Override
    public void disconnect(){
        if(hasConnection()) {
            System.out.println("sending ID on disconnect as:" + this.clientId);
            sendMessage(new Message("", this.clientId, "userLeaving"));
            System.out.println("I have left");
        }
    }
    @Override
    public void sendMessage(Message m) {
        sendObject(m);
    }

    @Override
    public boolean hasConnection(){
        return sr != null;
    }

    @Override
    public void getConnection(String ip, int port) {
        numberOfAttempts++;
        boolean connected = false;
        System.out.println("attempting connection");
        try {
            connected = establishConnection(ip, port);
        } catch (IOException e){
            e.printStackTrace();
            //System.exit(0);
        }
        if(connected) {
            try {
                startRuntimeChat();
            } catch (IOException e) {
                e.printStackTrace();
                //System.exit(0);
            }
        }
    }
    public void setName(String name){
        this.clientName = name;
    }


}
