import java.util.UUID;

public interface Host {

    void sendMessage(Message m);
    void disconnect();
    void stop();
    boolean hasConnection();
    void getConnection(String ip, int port);
    void setName(String name);
}
