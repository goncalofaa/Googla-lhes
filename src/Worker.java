import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Worker{

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String workerID;
    private InetAddress inetAddress_serverAdress;

    public Worker() throws UnknownHostException {
       this.inetAddress_serverAdress= InetAddress.getByName("localhost");
    }

    public Worker(InetAddress inetAddress_serverAdress){
        this.inetAddress_serverAdress=inetAddress_serverAdress;
    }

    public void runWorker() throws InterruptedException{
        try {
            while (true){
                    connectToServer();
                    break;
            }
            while (true){
                Task t = (Task) in.readObject();
                ArrayList<Integer> stringCoordinates = filterNews(t);
                out.writeObject(stringCoordinates);
            }
        } catch (Exception e){
            System.out.println("Lost Connection");
            e.printStackTrace();
            //Tenta dar recconect
            int i=0;
            while (i<10){
                try {
                    Thread.sleep(2000);
                    runWorker();
                }catch (Exception e1){
                    e1.printStackTrace();
                }
                i++;
            }
        }

    }

    public void connectToServer() throws IOException, ClassNotFoundException {
        Socket socket = new Socket(inetAddress_serverAdress, Server.PORT);
        System.out.println("Endereço: " + inetAddress_serverAdress + " |  Socket: " + socket);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        //Envia ao server um int 2 para o server saber que é um worker(1 = Client e 2 = Worker)
        out.writeObject(2);
        int workerNum = (int) in.readObject();
        workerID = "Worker"+ String.valueOf(workerNum);
        System.out.println("Worker: "+workerID);
    }

    public ArrayList<Integer> filterNews(Task task){
        ArrayList<Integer> result = new ArrayList<>();
        String string_aux = task.getNews().getBody();
        //procura a primeira coordenada de cada ocorrencia
        for (int i= -1; (i= string_aux.indexOf(task.getString(), i+1)) != -1; i++){
            result.add(i);
        }
        return result;
    }


}
