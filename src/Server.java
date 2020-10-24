import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    public static final int PORT = 8080;
    private static ArrayList<News> arrayList_news;
    private static TaskBlockingQueue taskBlockingQueue_toFilter, taskBlockingQueue_filtered;
    private ServerSocket serverSocket;
    private static String path = (System.getProperty("user.dir")+System.getProperty("file.separator")+"news");
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int clientCount;
    private int workerCount;
    private static int taskCount;

    public Server(){
        //Para usar path já definida
    }

    public Server(String path){
        this.path=path;
    }

    public void start() throws IOException, ClassNotFoundException {
        importNews();
        taskBlockingQueue_toFilter = new TaskBlockingQueue();
        taskBlockingQueue_filtered = new TaskBlockingQueue();
        clientCount=0;
        workerCount=0;
        taskCount=0;
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server: Started: "+ serverSocket);
        while (true){
            Socket socket = serverSocket.accept();
            try{
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
            }catch (IOException e){
                e.printStackTrace();
            }
            int r = (int) in.readObject();
            //Server recebe um int 1 ou 2, se r = 1 Client / se r = 2 Worker
            System.out.println("Server: received: " + r);
            //Cria clienthandler
            if (r == 1) {
                clientCount++;
                out.writeObject(clientCount);
                String clientID = "Client"+String.valueOf(clientCount);
                ClientHandler clientHandler = new ClientHandler(socket, in, out, clientID);
                clientHandler.start();
            }
            //Cria workerhandler
            if (r == 2){
                workerCount++;
                out.writeObject(workerCount);
                String workerID = "Worker"+String.valueOf(workerCount);
                WorkerHandler workerHandler = new WorkerHandler(socket, in, out, workerID);
                workerHandler.start();
            }
        }
    }



    public static void importNews(){
        arrayList_news = new ArrayList<>();
        File[] files = new File(path).listFiles();
        assert files != null;
        if (files.length <1 ){
            throw new IllegalArgumentException("No files");
        }
        for( File f: files){
            try {
                Scanner scan=new Scanner(f, "UTF-8");
                String titulo=scan.nextLine();
                StringBuilder corpo=new StringBuilder();

                while(scan.hasNext()) {
                    corpo.append(scan.nextLine()).append("\n");
                }

                scan.close();
                arrayList_news.add(new News(titulo, corpo.toString()));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


    }

    //Gera tasks a partir das noticias armazenadas no server
    public static void generateTasksToFilter(String string_filter, String clientID)  {
        try{
            for (News news: arrayList_news){
                News n = new News(news.getTitle(),news.getBody());
                taskBlockingQueue_toFilter.offer(new Task(n, string_filter, clientID, taskCount));
                taskCount++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Task pollTaskToFilter() throws InterruptedException {
       return taskBlockingQueue_toFilter.poll();
    }

    public static void addFilteredTask(Task t) throws InterruptedException {
        taskBlockingQueue_filtered.offer(t);
    }

    public static Task pollFilteredTask(String ClientID) throws InterruptedException {
        Task poll = taskBlockingQueue_filtered.poll(ClientID);
        return poll;
    }

    public static int numberOfNews(){
        return arrayList_news.size();
    }

    public static int numberOfFilteredTasks(){
        return taskBlockingQueue_filtered.getNumberOfTasks();
    }

    //Limpa tasks de clients que se tenham desconectado a meio de uma procura
    public static void clearDisconnectedClientTasks(String clientID){
        try {
            taskBlockingQueue_filtered.removeDisconnectClientTasks(clientID);
            taskBlockingQueue_toFilter.removeDisconnectClientTasks(clientID);
            System.out.println("FINAL DA LIMPEZA");
            inQueue();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //Sys.ou de controlo de tasks em queue
    public static void inQueue(){
        System.out.println("Filtered:"+taskBlockingQueue_filtered.getNumberOfTasks()+"|NonFiltered:"+taskBlockingQueue_toFilter.getNumberOfTasks());
    }

    //Repor task que o worker não chegou a enviar
    public static void addTaskToFilter(Task t) throws InterruptedException {
        taskBlockingQueue_toFilter.offer(t);
    }


}
