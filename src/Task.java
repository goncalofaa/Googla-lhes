import java.io.Serializable;
import java.util.ArrayList;

public class Task implements Serializable {

    private News news;
    private String string;
    private ArrayList<Integer> result;
    private String clientID;
    private int taskID;

    public Task(News news, String string, String clientID, int taskID){
        this.news=news;
        this.string=string;
        this.clientID=clientID;
        //task id serve só para controlo de erros através de sys.outs
        this.taskID=taskID;
    }

    public News getNews(){
        return this.news;
    }

    public String getString(){
        return this.string;
    }

    public void setResult(ArrayList<Integer> result){
        this.result=result;
    }

    public String getClientID(){
        return this.clientID;
    }

    public ArrayList<Integer> getResult(){
        return this.result;
    }

}
