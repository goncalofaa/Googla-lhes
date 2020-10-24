import java.io.Serializable;
import java.util.ArrayList;

public class News implements Serializable {

    private final String title;
    private final String body;
    private String string_filter;
    private ArrayList<Integer> results;
    private int results_count;

    public News(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return this.title;
    }

    public String getBody() {
        return this.body;
    }

    public void setArrayListStringCoordinates(ArrayList<Integer> arrayList){
        this.results=arrayList;
    }

    public ArrayList<Integer> getArrayList(){
        return this.results;
    }

    public int getResultsNumber(){
        return this.results.size();
    }

    public void setResults_count(){
        this.results_count=getResultsNumber();
    }
}
