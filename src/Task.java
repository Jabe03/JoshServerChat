import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class Task extends File{
    static ArrayList<Task> taskList = new ArrayList<Task>();
    private String name;
    private String date;
    private String time;
    private String desc;





    private Task(String name){
        super("lib\\tasks\\" + name + ".txt");
        try{
            if(createNewFile()){
                System.out.println("new file created");
            } else {
                System.out.println("existing file found");
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        taskList.add(this);
    }
    public Task(File f){
        super(f.getName());
        getFileInfo();
    }
    private String getInfo(String type){
        try (Scanner tsm = new Scanner(this)){
            tsm.useDelimiter("%"+type+"%");
            String result;
            do {
                result =tsm.next();
            }  while(result.charAt(0) == '%');
            return result;
        } catch (FileNotFoundException e){
            e.printStackTrace();
            return "null";
        }
    }
    private void getFileInfo(){

        this.desc = getInfo("desc");
        this.name = getName();
        this.date = getInfo("date");
        this.time = getInfo("time");

    }

    public static Task createNewTask(String name, String date, String time, String desc){
        Task t = new Task(name);
        try(PrintWriter out = new PrintWriter(t)){
        out.println("%date%" + date + "%date%");
        out.println("%time%"+ time + "%time%");
        out.println("%desc%"+desc + "%desc%");
        } catch(FileNotFoundException e){
            System.out.println("error");
            e.printStackTrace();
        }
        t.getFileInfo();
        return t;
    }

    static Date today(){
        return Calendar.getInstance().getTime();
    }

    public static void removeTask(Task t){
            taskList.remove(t);
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDesc() {
        return desc;
    }
    public String toString(){
        return "Task[Name: " + name +", Date: " + date + ", Time: " + time + ", Description: " + desc+"]";
    }
}
