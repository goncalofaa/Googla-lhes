import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClientHandler extends Thread{

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String clientID;
    private ArrayList<Task> results;
    private ArrayList<News> arrayList_filteredNews;

    public ClientHandler(Socket socket, ObjectInputStream in, ObjectOutputStream out, String clientID){
        this.in=in;
        this.out=out;
        this.clientID=clientID;
    }

    public void run(){
        try {
            while (true){
                results = new ArrayList<>();
                arrayList_filteredNews = new ArrayList<>();
                System.out.println(clientID+": Client Handler On");
                //Recebe a String
                String s= (String) in.readObject();
                System.out.println(clientID+": String Received: "+s);
                try {
                    Server.generateTasksToFilter(s, clientID);
                    //Até ter o mesmo numero de noticias que o server tem de noticias na pasta news, fica a dar poll
                    while (true){
                        results.add(Server.pollFilteredTask(clientID));
                        if(results.size() == Server.numberOfNews()){
                            break;
                        }
                    }
                    System.out.println("Server: Left the while CH:"+clientID);
                    //Comparador usa o sizer do numero de resultados de cada arraylist para ordenar por ordem decrescente
                    Collections.sort(results, new Comparator<Task>() {
                        @Override
                        public int compare(Task o1, Task o2) {
                            int i1=o1.getResult().size();
                            int i2=o2.getResult().size();
                            return  Integer.compare(i2,i1);
                        }
                    });
                    //Conversao das tasks com mais de 0 ocorrencias da string em news, já ordenadas
                    for (Task t: results){
                        if(t.getResult().size()>0){
                            t.getNews().setResults_count();
                            arrayList_filteredNews.add(t.getNews());
                        }
                    }
                    out.writeObject(arrayList_filteredNews);
                    System.out.println(clientID+": Array dispatched");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (SocketException e){
            //Se for interrompida e tiver tasks por processar nas queus, via tirá-las das queues
            Server.clearDisconnectedClientTasks(clientID);
            System.out.println(clientID+": Client Disconnected Cleared Tasks");
        } catch (EOFException e){
            e.printStackTrace();
            System.out.println(clientID+": Client Disconnected");
        } catch (IOException e){
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
