import java.io.IOException;

public class WorkerMain {

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Worker worker=new Worker();
        worker.runWorker();
    }
}
