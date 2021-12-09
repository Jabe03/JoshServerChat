import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

public class ClientHandler {
    String name;
    UUID id;
    Socket s;
    InputStream in;
    OutputStream out;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    boolean running;

    public ClientHandler(Socket s) {
        this.s = s;
        this.id = UUID.randomUUID();
        try {
            in = s.getInputStream();
            out = s.getOutputStream();
            ois = new ObjectInputStream(in);
            oos = new ObjectOutputStream(out);
        } catch(IOException e){
            e.printStackTrace();
        }

        this.name = getClientName();
        listenForInputs();
    }
    private String getClientName(){
        String name = null;
        try {
            Object o = ois.readObject();
            System.out.println("Name object: " + o);
            if (o instanceof Message){
                name = ((Message) o).getText();
                System.out.println("Name received: " + name);
            } else {
                oos.writeObject("%error%Name expected, " + o.getClass() + " got... Disconnecting");
                s.close();
            }
            sendObject(id);

        } catch (ClassNotFoundException | IOException e){
            e.printStackTrace();
            return null;
        }
        return name;
    }

    public void listenForInputs(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                running = true;
                while(running){
                    try {
                        Object message = ois.readObject();
                        Server.getInstance().addPacket(message);
                    } catch(SocketException e){
                        running = false;
                    } catch(IOException | ClassNotFoundException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        t.setName("client " + this.name + " listener");
        t.start();
    }

    @Override
    public String toString(){
        return "RemoteClient[name: " + this.name + ", id: " + id + "]";
    }
    public synchronized void sendObject(Object o){
        try {

            System.out.println("(77)Sending object: " + o);
            oos.writeUnshared(o);
            System.out.println("Done sending: " + o );
            oos.reset();
        } catch(IOException e){
            e.printStackTrace();
        }
    }


}
