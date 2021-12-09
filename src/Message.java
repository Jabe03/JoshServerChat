import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {

    Object objectMessage;
    String text;
    UUID id;
    private boolean isTextMessage;

    public Message(String text, UUID id){
        this.text = text;
        this.id = id;
        isTextMessage = true;
    }
    public Message(Object msg, UUID id, String command){
        this.objectMessage = msg;
        this.text = command;
        this.id = id;
        isTextMessage = false;
    }
    public Message(String text){
        this.text = text;
    }
    public UUID getID(){ return this.id;}
    public String getText(){
        return this.text;
    }
    public Object getObjectMessage(){
        return this.objectMessage;
    }

    public boolean isTextMessage(){
        return isTextMessage;
    }

    public String toString(){
        if(isTextMessage){
            return "[Message:" + text + ", ID: " + id + "]";
        }
        return "[Message: Object:" + this.objectMessage + ", command: " + this.text + ", ID: " + id + "]";
    }



}
