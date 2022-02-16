import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements Closeable, Host{

    private boolean running;

    public static void main(String[] args) {
        new Server();
    }

    ServerSocket ss;
    Socket sr;
    OutputStream os;

    ChatWindow cw;
    private static Server instance;
    private static final UUID serverID = UUID.randomUUID();
    final private ArrayList<ClientHandler> clients;
    volatile ArrayList<Object> packets;

    private Server() {

        instance = this;
        clients = new ArrayList<>();
        packets = new ArrayList<>();
        startServer();
    }

    public void startServer() {
        cw = new ChatWindow("server", this,serverID);

    }
    public void addPacket(Object p){
        packets.add(p);

    }
    public static boolean isServerId(UUID id){
        return id.equals(serverID);
    }
    public void addClient(ClientHandler c) {
        clients.add(c);
        cw.addUserById(c.id,c.name);
    }
    public static Server getInstance(){
        return instance;
    }
    @Override
    public void close() throws IOException {
        os.close();
        sr.close();
        ss.close();
    }

    public void getConnection(int port) throws IOException {
        ss = new ServerSocket(port);
        running = true;
        Thread t = new Thread(() -> {
            while (running) {
                try {
                    sr = ss.accept();
                    System.out.println("accepted");
                    addClient(new ClientHandler(sr));
                    packets.add("Starting char");
                    updateClients();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        t.setName("ConnectionSearching");
        t.start();
    }
    public synchronized void startProcessing(){
        Thread t = new Thread(() -> {
            while (true) {
                processPackets();
            }
        });
        t.setName("ServerProcess");
        t.start();
    }

    private void processPackets(){
        //System.out.println("About to check...");
                while (!packets.isEmpty()) {
                    Object packet = packets.get(0);
                    System.out.println("\u001B[34mPacket received! " + packet + "\u001B[0m");
                    if(packet instanceof Message m){
                        if(m.isTextMessage()){
                            if(m.getID().equals(serverID)){
                                cw.addMessage(new Message(m.getText(), serverID, "serverMessage"));
                            } else {
                                cw.addMessage(m);
                            }


                        } else{
                            switch (m.getText()) {
                                case "updateMessages" -> sendOutUpdatedMessageList();
                                case "userJoining", "userLeaving" ->
                                        cw.addMessage(new Message(m.getID(), serverID, m.getText()));
                                default -> cw.addMessage(new Message("serverMessage", serverID, "Unknown command: " + m.getText()));
                            }
                        }


                    }
                    packets.remove(0);
                }


    }

    public void updateClients(){
        for(ClientHandler c: clients){
            c.sendObject(new Message(cw.getParticipants(), serverID, "updatedClients"));
        }
    }
    public void sendOutUpdatedMessageList(){

        for(ClientHandler c: clients){
            c.sendObject(new Message(cw.messages, serverID, "updatedMessages"));
        }
    }


    @Override
    public void sendMessage(Message m) {
        addPacket(m);
    }
    @Override
    public void disconnect(){

    }
    public void stop(){
        System.exit(1);
    }

    @Override
    public boolean hasConnection() {
        return running;
    }

    @Override
    public void getConnection(String ip, int port) {
        startProcessing();
        try {
            getConnection(port);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setName(String name){

    }

}
