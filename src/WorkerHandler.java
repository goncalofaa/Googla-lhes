import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class WorkerHandler extends Thread {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String workerID;
    private Task task;

    public WorkerHandler(Socket socket, ObjectInputStream in, ObjectOutputStream out, String workerID){
        this.in=in;
        this.out=out;
        this.workerID=workerID;
    }

    public void run(){
            Boolean treatedTask = null;
            System.out.println(workerID+": Worker Handler On");
            try {
                while (true){
                        //Fica a dar pol da queu das tasks por filtrar
                        this.task = Server.pollTaskToFilter();
                        treatedTask=false;
                        out.writeObject(task);
                        ArrayList<Integer> results = (ArrayList<Integer>) in.readObject();
                        this.task.setResult(results);
                        this.task.getNews().setArrayListStringCoordinates(results);
                        Server.addFilteredTask(task);
                        treatedTask=true;
                }
            } catch(Exception e){
                System.out.println(workerID+":Disconnected");
                e.printStackTrace();
            }
            try {
                if(!treatedTask){
                    //Se for interrompida antes de enviar a task tratada repoe a task na queue
                    System.out.println(workerID+":Disconnected and Reposted Task");
                    Server.addTaskToFilter(task);
                }
            } catch (Exception e) {
                System.out.println("Disconnected without repost");
                e.printStackTrace();
            }

    }


}
