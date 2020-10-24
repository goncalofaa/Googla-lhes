import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TaskBlockingQueue {

    private ArrayList<Task> data;
    private final int MAX = 5000;

    public TaskBlockingQueue() {
        if (MAX <= 0) {
            throw new IllegalStateException();
        }
        this.data=new ArrayList<Task>();
    }

    public synchronized void offer(Task task) throws InterruptedException {
        while (data.size() >= MAX) {
            wait();
        }
        data.add(task);
        notifyAll();
    }

    public synchronized Task poll() throws InterruptedException{
        while (data.size() <= 0){
            wait();
        }
        Task t= data.remove(0);
        notifyAll();
        return t;
    }

    //retira task com um certo clientID
    public synchronized Task poll(String clientID) throws InterruptedException {
        while(data.size() <=0 || data.get(0).getClientID() != clientID){
            wait();
        }
        Task t= data.remove(0);
        notifyAll();
        return t;
    }

    public synchronized int getNumberOfTasks(){
        return this.data.size();
    }

    //remove tasks de clients que se desconectaram
    public synchronized void removeDisconnectClientTasks(String clientID) throws InterruptedException {
        while(data.size()<=0){
            wait();
        }
        for(int i=0;i<data.size()-1;i++){
            if (data.get(i).getClientID()==clientID){
                data.remove(i);
            }
        }
        notifyAll();
    }

}
